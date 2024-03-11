package com.nbcampif.ifstagram.domain.report.controller;


import com.nbcampif.ifstagram.domain.report.dto.ReportRequestDto;
import com.nbcampif.ifstagram.domain.report.dto.ReportResponseDto;
import com.nbcampif.ifstagram.domain.report.service.ReportService;
import com.nbcampif.ifstagram.domain.user.model.User;
import com.nbcampif.ifstagram.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/reports/user")
@RestController
public class ReportController {

  private final ReportService reportService;
  //신고기능

  @Operation(summary = "회원 신고", description = "회원 신고")
  @PostMapping("/{userId}")
  public ResponseEntity<CommonResponse<ReportResponseDto>> reportUser(
    @PathVariable Long userId,
    @AuthenticationPrincipal User user,
    @RequestBody ReportRequestDto requestDto) {
    ReportResponseDto response = reportService.reportUser(userId, user, requestDto);
    return ResponseEntity.status(HttpStatus.OK)
      .body(CommonResponse.<ReportResponseDto>builder()
        .message("신고완료되었습니다.")
        .data(response)
        .build());
  }
}
