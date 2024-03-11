package com.nbcampif.ifstagram.domain.user.controller;

import com.nbcampif.ifstagram.domain.user.dto.UserResponseDto;
import com.nbcampif.ifstagram.domain.user.dto.UserUpdateRequestDto;
import com.nbcampif.ifstagram.domain.user.model.User;
import com.nbcampif.ifstagram.domain.user.service.UserService;
import com.nbcampif.ifstagram.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserController {

  private final UserService userService;
  //신고기능

  @GetMapping("/my-page")
  public ResponseEntity<CommonResponse<UserResponseDto>> myPage(
      @AuthenticationPrincipal User user
  ) {
    return CommonResponse.ok("조회 성공", UserResponseDto.of(user));
  }

  @PostMapping("/{userId}/follow")
  public ResponseEntity<CommonResponse<Void>> follow(
      @PathVariable(name = "userId") Long toUserId, @AuthenticationPrincipal User user
  ) {
    userService.follow(user, toUserId);

    return CommonResponse.ok("팔로우 성공", null);
  }

  @PutMapping("/my-page")
  public ResponseEntity<CommonResponse<UserResponseDto>> updateUser(
      @Validated @RequestBody UserUpdateRequestDto requestDto, @AuthenticationPrincipal User user
  ) {
    User updatedUser = userService.updateUser(requestDto, user);

    return CommonResponse.ok("수정 성공", UserResponseDto.of(updatedUser));
  }

  @Operation(summary = "회원 신고", description = "회원 신고")
  @PostMapping("/reports/{userId}")
  public ResponseEntity<CommonResponse<Void>> reportUser(
      @PathVariable Long userId
  ) {
    System.out.println(userId);
    return userService.reportUser(userId);
  }

}
