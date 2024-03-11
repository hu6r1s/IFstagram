package com.nbcampif.ifstagram.domain.admin.controller;

import com.nbcampif.ifstagram.domain.admin.dto.LoginRequestDto;
import com.nbcampif.ifstagram.domain.admin.dto.UserForceUpdateRequestDto;
import com.nbcampif.ifstagram.domain.admin.service.AdminService;
import com.nbcampif.ifstagram.domain.post.dto.PostRequestDto;
import com.nbcampif.ifstagram.domain.post.dto.PostResponseDto;
import com.nbcampif.ifstagram.domain.user.dto.ReportReponseDto;
import com.nbcampif.ifstagram.domain.user.dto.UserResponseDto;
import com.nbcampif.ifstagram.domain.user.model.User;
import com.nbcampif.ifstagram.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Admin Controller", description = "관리자 컨트롤러")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

  private final AdminService adminService;

  @Operation(summary = "관리자 로그인", description = "관리자 로그인")
  @PostMapping("/login")
  public ResponseEntity<CommonResponse<Void>> adminLogin(
    @RequestBody LoginRequestDto requestDto,
    HttpServletResponse response
  ) {
    adminService.login(requestDto, response);
    return ResponseEntity.status(HttpStatus.OK.value()).body(
      CommonResponse.<Void>builder().message("로그인 성공").build()
    );
  }

  @Operation(summary = "유저 조회", description = "관리자가 유저를 조회할 수 있는 API")
  @GetMapping("/user/{userId}")
  public ResponseEntity<CommonResponse<UserResponseDto>> searchUser(
    @PathVariable Long userId,
    @AuthenticationPrincipal User admin
  ) {
    UserResponseDto responseDto = adminService.searchUser(userId);
    return ResponseEntity.status(HttpStatus.OK.value()).body(
      CommonResponse.<UserResponseDto>builder()
        .message("조회 성공")
        .data(responseDto)
        .build()
    );
  }

  @Operation(summary = "신고 내역 조회", description = "관리자가 신고 내역을 조회할 수 있는 API")
  @GetMapping("/report/{reportId}")
  public ResponseEntity<CommonResponse<List<ReportReponseDto>>> searchReport(
    @PathVariable Long reportId,
    @AuthenticationPrincipal User admin
  ) {
    List<ReportReponseDto> reponseList = adminService.searchReport(reportId);
    return ResponseEntity.status(HttpStatus.OK.value()).body(
      CommonResponse.<List<ReportReponseDto>>builder()
        .message("신고 내역 조회 성공")
        .data(reponseList)
        .build()
    );
  }

  @Operation(summary = "유저 정보 수정", description = "관리자가 유저의 정보를 수정할 수 있는 API")
  @PutMapping("/user/{userId}")
  public ResponseEntity<CommonResponse<Void>> updateUser(
    @PathVariable Long userId,
    @RequestBody UserForceUpdateRequestDto requestDto,
    @AuthenticationPrincipal User admin
  ) {
    adminService.updateUser(userId, requestDto);
    return ResponseEntity.status(HttpStatus.OK.value()).body(
      CommonResponse.<Void>builder()
        .message("유저 정보 수정 성공")
        .build()
    );
  }

  @Operation(summary = "게시글 수정", description = "관리자가 게시글을 수정할 수 있는 API")
  @PutMapping("/post/{postId}")
  public ResponseEntity<CommonResponse<Void>> updatePost(
    @PathVariable Long postId,
    @RequestPart(value = "data") PostRequestDto requestDto,
    @RequestPart(value = "file") MultipartFile image,
    @AuthenticationPrincipal User admin
  ) throws IOException {
    adminService.updatePost(postId, requestDto, image);
    return ResponseEntity.status(HttpStatus.OK.value()).body(
      CommonResponse.<Void>builder()
        .message("게시글 수정 성공")
        .build()
    );
  }

  @Operation(summary = "게시글 삭제", description = "관리자가 게시글을 삭제할 수 있는 API")
  @DeleteMapping("/post/{postId}")
  public ResponseEntity<CommonResponse<Void>> deletePost(
    @PathVariable Long postId,
    @AuthenticationPrincipal User admin
  ) {
    adminService.deletePost(postId);
    return ResponseEntity.status(HttpStatus.OK.value()).body(
      CommonResponse.<Void>builder()
        .message("게시글 삭제 성공")
        .build()
    );
  }

  @Operation(summary = "삭제된 게시글 조회", description = "관리자가 삭제된 게시글을 조회할 수 있는 API")
  @GetMapping("/posts/deleted")
  public ResponseEntity<CommonResponse<List<PostResponseDto>>> getDeletedPost() {
    List<PostResponseDto> responseList = adminService.getDeletedPost();
    return ResponseEntity.status(HttpStatus.OK.value()).body(
      CommonResponse.<List<PostResponseDto>>builder()
        .message("삭제된 게시글 조회")
        .data(responseList)
        .build()
    );
  }

  @Operation(summary = "이벤트 조회", description = "좋아요, 팔로우를 많이 사용한 유저들을 조회할 수 있는 API")
  @GetMapping("/event")
  public ResponseEntity<CommonResponse<List<UserResponseDto>>> getEvent() {
    List<UserResponseDto> responseList = adminService.getEvent();
    return ResponseEntity.status(HttpStatus.OK.value()).body(
      CommonResponse.<List<UserResponseDto>>builder()
        .message("이벤트 조회")
        .data(responseList)
        .build()
    );
  }
}
