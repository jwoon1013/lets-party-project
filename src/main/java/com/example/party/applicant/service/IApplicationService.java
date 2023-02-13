package com.example.party.applicant.service;

import com.example.party.global.dto.DataResponseDto;
import com.example.party.global.dto.ListResponseDto;
import com.example.party.global.dto.ResponseDto;
import com.example.party.user.entity.User;

public interface IApplicationService {
	// 모집 참가 신청
	DataResponseDto<?> createApplication();

	//모집 참가 신청취소
	ResponseDto cancelApplication(Long applicationId, User user);
	//참가신청자 리스트 조회(파티장 미포함. 내정보-나의모집글목록 상에서 사용)

	ListResponseDto<?> getApplications(Long partPostId);

	//모집 참가 수락
	DataResponseDto<?> acceptApplication(Long applicationId, User user);

	//모집 참가 거부
	DataResponseDto<?> rejectApplication(Long applicationId, User user);

}