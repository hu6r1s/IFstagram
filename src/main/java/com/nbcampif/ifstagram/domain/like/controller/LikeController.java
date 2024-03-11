package com.nbcampif.ifstagram.domain.like.controller;

import com.nbcampif.ifstagram.domain.like.service.LikeService;
import com.nbcampif.ifstagram.domain.user.model.User;
import com.nbcampif.ifstagram.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class LikeController {

  private final LikeService likeService;

  @PostMapping("/{postId}/likes")
  public ResponseEntity<CommonResponse<Void>> countLike(
      @PathVariable Long postId,
      @AuthenticationPrincipal User user) {
    Long count = likeService.countLike(postId, user);

    return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.<Void>builder()
      .message("좋아요 " + count + "개").build());
  }
}
