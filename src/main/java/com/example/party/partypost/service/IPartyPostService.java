package com.example.party.partypost.service;

import org.springframework.transaction.annotation.Transactional;

import com.example.party.global.common.ApiResponse;
import com.example.party.global.common.DataApiResponse;
import com.example.party.global.common.ItemApiResponse;
import com.example.party.partypost.dto.MyPartyPostListResponse;
import com.example.party.partypost.dto.PartyPostListResponse;
import com.example.party.partypost.dto.PartyPostRequest;
import com.example.party.partypost.dto.PartyPostResponse;
import com.example.party.partypost.dto.SearchPartyPostListResponse;
import com.example.party.partypost.dto.UpdatePartyPostRequest;
import com.example.party.user.entity.User;

public interface IPartyPostService {

	//모집글 작성
	ItemApiResponse<PartyPostResponse> createPartyPost(User user, PartyPostRequest request);

	//모집글 수정
	ItemApiResponse<PartyPostResponse> updatePartyPost(Long partyPostId, UpdatePartyPostRequest request, User user);

	//내가 작성한 모집글 리스트 조회 ( 내가 파티장인 경우만 )
	DataApiResponse<MyPartyPostListResponse> findMyCreatedPartyList(User user, int page);

	//내가 신청한 모집글 리스트 조회( 내가 파티원인 경우만 )
	DataApiResponse<MyPartyPostListResponse> findMyJoinedPartyList(User user, int page);

	//모집게시물 좋아요 (*좋아요 취소도 포함되는 기능임)
	ItemApiResponse<String> toggleLikePartyPost(Long party_postId, User user);

	//모집글 삭제
	ApiResponse deletePartyPost(Long partyPostId, User user);

	//모집글 전체 조회
	@Transactional
	DataApiResponse<PartyPostListResponse> findPartyList(int page, User user);

	//모집글 상세 조회(개별 상세조회)
	@Transactional
	ItemApiResponse<PartyPostResponse> getPartyPost(Long postId, User user);

	// 검색단어로 모집글 리스트 조회
	DataApiResponse<SearchPartyPostListResponse> searchPartyPost(String searchText, int page);

	DataApiResponse<SearchPartyPostListResponse> findHotPartyPost();

	//카테고리명 별로 모집글 조회
	DataApiResponse<PartyPostListResponse> searchPartyPostByCategory(Long categoryId, int page);

	// 읍면동으로 가까운 모집글 리스트 조회
	DataApiResponse<SearchPartyPostListResponse> findNearPartyPost(String eubMyeonDong);
}
