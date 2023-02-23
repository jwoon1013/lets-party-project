package com.example.party.application.service;

import java.time.LocalDateTime;

import com.example.party.partypost.entity.Parties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.party.application.dto.ApplicationResponse;
import com.example.party.application.entity.Application;
import com.example.party.application.exception.ApplicationNotAvailableException;
import com.example.party.application.exception.ApplicationNotFoundException;
import com.example.party.application.exception.ApplicationNotGeneraleException;
import com.example.party.application.repository.ApplicationRepository;
import com.example.party.application.type.ApplicationStatus;
import com.example.party.global.common.ApiResponse;
import com.example.party.global.common.DataApiResponse;
import com.example.party.global.exception.ForbiddenException;
import com.example.party.partypost.entity.PartyPost;
import com.example.party.partypost.exception.PartyPostNotFoundException;
import com.example.party.partypost.repository.PartyPostRepository;
import com.example.party.partypost.repository.PartyRepository;
import com.example.party.user.entity.User;
import com.example.party.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class ApplicationService implements IApplicationService {

	private final ApplicationRepository applicationRepository;
	private final PartyPostRepository partyPostRepository;
	private final UserRepository userRepository;
	private final PartyRepository partyRepository;

	//모집글에 참가 신청
	@Override
	public ApiResponse createApplication(Long partyPostId, User user) {
		//0. 받아온 user 를 영속성 컨텍스트에 저장
		User user1 = userRepository.save(user);

		//1. partyPost 불러오기
		PartyPost partyPost = partyPostRepository.findById(partyPostId).orElseThrow(
			PartyPostNotFoundException::new
		);
		//2. Application 이 작성 가능한지 검증
		checkBeforeCreateApplication(partyPost, user1);
		//3. Application 객체 생성
		Application application = new Application(user1, partyPost);

		//4. repository 에 save
		applicationRepository.save(application);

		//5. 각 객체의 List 에 Application 저장
		partyPost.addApplication(application);
		user1.addApplication(application);

		//6.  DataResponseDto 생성 후 return
		return ApiResponse.ok("참가 신청 완료");

	}

	//참가신청 취소
	@Override
	public ApiResponse cancelApplication(Long applicationId, User user) {
		Application application = getApplication(applicationId);

		if (!application.isWrittenByMe(user.getId())) {
			throw new ForbiddenException();
		}
		application.cancel();

		return ApiResponse.ok("참가 신청 취소 완료");
	}

	//모집글의 참가신청 목록 조회(파티장만 조회가능)
	@Transactional(readOnly = true)
	@Override
	public DataApiResponse<ApplicationResponse> getApplications(Long partPostId, User user) {
		PartyPost partyPost = partyPostRepository.findById(partPostId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "NOT FOUND"));

		if (!partyPost.isWrittenByMe(user.getId())) {
			throw new ForbiddenException();
		}

		Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
		Page<ApplicationResponse> ret = applicationRepository.findAllByPartyPostAndCancelIsFalse(
				partyPost,
				pageable)
			.map(ApplicationResponse::new);

		return DataApiResponse.ok("참가신청자 목록 조회 완료", ret.getContent());
	}

	//(파티장) 참가신청 수락
	@Override
	public ApiResponse acceptApplication(Long applicationId, User user) {
		Application application = getApplication(applicationId);

		if (!application.isSendToMe(user.getId())) {
			throw new ForbiddenException();
		}

		validateApplication(application);
		application.accept();

		//Accept 된 유저만 넘어감
		Parties parties = partyRepository.findById(application.getPartyPost().getId())
			.orElseThrow(PartyPostNotFoundException::new);
		parties.addUsers(user);
		partyRepository.save(parties);

		return ApiResponse.ok("참가 신청 수락 완료");
	}

	//(파티장) 참가신청 거부
	@Override
	public ApiResponse rejectApplication(Long applicationId, User user) {
		Application application = getApplication(applicationId);

		if (!application.isSendToMe(user.getId())) {
			throw new ForbiddenException();
		}

		validateApplication(application);
		application.reject();

		return ApiResponse.ok("참가 신청 거부 완료");
	}

	//단일 참가신청 객체 불러오기
	@Transactional(readOnly = true)
	public Application getApplication(Long applicationId) {
		return applicationRepository.findById(applicationId)
			.orElseThrow(ApplicationNotFoundException::new);
	}

	//참가신청 작성 전 조건 검증 메소드
	private void checkBeforeCreateApplication(PartyPost partyPost, User user) {
		//(1) 내가 작성자인지 확인
		if (partyPost.isWrittenByMe(user.getId())) {
			throw new ApplicationNotGeneraleException("내가 작성한 모집글에 참가신청할 수 없습니다");
		}

		//(2) partyPost 가 모집마감시간전인지 확인
		if (!partyPost.beforeCloseDate(LocalDateTime.now())) {
			throw new ApplicationNotGeneraleException("모집 마감시간이 지나, 참가신청할 수 없습니다");
		}
		//(3) partyPost 가 FINDING 인지 확인
		if (!partyPost.isFinding()) {
			throw new ApplicationNotGeneraleException("모집글이 모집 중인 상태가 아닙니다");
		}

		//(4) 중복검사
		if (applicationRepository.existsByPartyPostAndUser(partyPost, user)) {
			throw new ApplicationNotGeneraleException("이미 신청한 모집글입니다");
		}
	}

	//참가신청이 PENDING(대기중) 상태인지 확인
	private static void validateApplication(Application application) {
		if (application.getStatus() != ApplicationStatus.PENDING) {
			throw new ApplicationNotAvailableException();
		}
	}
}
