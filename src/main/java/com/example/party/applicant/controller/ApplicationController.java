package com.example.party.applicant.controller;

import com.example.party.applicant.service.ApplicationService;
import com.example.party.applicant.type.ApplicationResponse;
import com.example.party.global.dto.ListResponseDto;
import com.example.party.global.dto.ResponseDto;
import com.example.party.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/applications")
@RestController
public class ApplicationController {

  private final ApplicationService applicationService;

  @PostMapping("join/{partyPostId}") // 모집 참가 신청
  public ResponseEntity<ResponseDto> createApplication(@PathVariable Long partyPostId,
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(applicationService.createApplication(partyPostId, user));
  }

  @PostMapping("/cancel/{applicationId}")
  public ResponseEntity<ResponseDto> cancelApplication(@PathVariable Long applicationId,
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(applicationService.cancelApplication(applicationId, user));
  }

  @GetMapping("/{party-postId}")
  public ResponseEntity<ListResponseDto<ApplicationResponse>> getApplications(
      @PathVariable(name = "party-postId") Long partyPostId, @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(applicationService.getApplications(partyPostId, user));
  }

  @PostMapping("/accept/{applicationId}")
  public ResponseEntity<ResponseDto> acceptApplication(
      @PathVariable Long applicationId,
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(applicationService.acceptApplication(applicationId, user));
  }

  @PostMapping("/reject/{applicationId}")
  public ResponseEntity<ResponseDto> rejectApplication(@PathVariable Long applicationId,
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(applicationService.rejectApplication(applicationId, user));
  }

}
