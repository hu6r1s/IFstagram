package com.nbcampif.ifstagram.domain.post.controller;

import com.nbcampif.ifstagram.domain.post.dto.PostRequestDto;
import com.nbcampif.ifstagram.domain.post.dto.PostResponseDto;
import com.nbcampif.ifstagram.domain.post.service.PostService;
import com.nbcampif.ifstagram.domain.user.model.User;
import com.nbcampif.ifstagram.global.response.CommonResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

  private final PostService postService;

  @PostMapping
  public ResponseEntity<CommonResponse<Void>> createPost(
    @RequestPart(value = "data") PostRequestDto requestDto,
    @RequestPart(value = "file", required = false) MultipartFile image,
    @AuthenticationPrincipal User user) throws Exception {
    postService.createPost(requestDto, image, user);
    return ResponseEntity.status(HttpStatus.OK.value()).body(
      CommonResponse.<Void>builder().message("게시글 생성").build());
  }

  @GetMapping
  public ResponseEntity<CommonResponse<List<PostResponseDto>>> getPostList() {
    List<PostResponseDto> responseDto = postService.getPostList();
    return ResponseEntity.status(HttpStatus.OK.value()).body(
      CommonResponse.<List<PostResponseDto>>builder()
        .message("게시글 전체 조회 성공")
        .data(responseDto)
        .build());
  }

  @GetMapping("/search")
  public ResponseEntity<CommonResponse<List<PostResponseDto>>> getCondPostList(
    @RequestParam(name = "title") String title
  ) {
    List<PostResponseDto> responseDto = postService.getCondPostList(title);
    return ResponseEntity.status(HttpStatus.OK.value()).body(
      CommonResponse.<List<PostResponseDto>>builder()
        .message("게시글 조건 조회 성공")
        .data(responseDto)
        .build());
  }

  @GetMapping("/{postId}")
  public ResponseEntity<CommonResponse<PostResponseDto>> getPost(
    @PathVariable Long postId) throws MalformedURLException {
    PostResponseDto responseDto = postService.getPost(postId);
    return ResponseEntity.status(HttpStatus.OK.value()).body(
      CommonResponse.<PostResponseDto>builder()
        .message("게시글 조회 성공")
        .data(responseDto)
        .build());
  }

  @PutMapping("/{postId}")
  public ResponseEntity<CommonResponse<Void>> updatePost(
    @PathVariable Long postId,
    @RequestPart(value = "data") PostRequestDto requestDto,
    @RequestPart(value = "file", required = false) MultipartFile image,
    @AuthenticationPrincipal User user
    ) throws IOException {
    postService.updatePost(postId, requestDto, image, user);
    return ResponseEntity.status(HttpStatus.OK.value()).body(
      CommonResponse.<Void>builder().message("게시글 수정 성공").build());
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<CommonResponse<Void>> deletePost(
    @PathVariable Long postId,
    @AuthenticationPrincipal User user
    ) {
    postService.deletePost(postId, user);
    return ResponseEntity.status(HttpStatus.OK.value()).body(
      CommonResponse.<Void>builder().message("게시글 삭제 성공").build());
  }

  @GetMapping("/follow")
  public ResponseEntity<CommonResponse<List<PostResponseDto>>> followPost(
    @AuthenticationPrincipal User user
  ) {
    List<PostResponseDto> postList = postService.followPost(user);
    return ResponseEntity.status(HttpStatus.OK.value()).body(
      CommonResponse.<List<PostResponseDto>>builder()
        .message("팔로우한 게시글 조회")
        .data(postList)
        .build());
  }
}
