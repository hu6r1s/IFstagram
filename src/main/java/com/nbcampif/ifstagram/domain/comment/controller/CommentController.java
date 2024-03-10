package com.nbcampif.ifstagram.domain.comment.controller;


import com.nbcampif.ifstagram.domain.comment.dto.CommentRequestDto;
import com.nbcampif.ifstagram.domain.comment.dto.CommentResponseDto;
import com.nbcampif.ifstagram.domain.comment.service.CommentService;
import com.nbcampif.ifstagram.domain.user.model.User;
import com.nbcampif.ifstagram.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments/post")
public class CommentController {

  private final CommentService commentService;

  @Operation(summary = "댓글 생성", description = "댓글 생성")
  @PostMapping("/{postId}")
  public ResponseEntity<CommonResponse<Void>> createComment(
    @PathVariable Long postId,
    @AuthenticationPrincipal User user,
    @RequestBody CommentRequestDto requestDto) {
    commentService.createComment(requestDto, postId, user);
    return ResponseEntity.status(HttpStatus.OK)
      .body(CommonResponse.<Void>builder()
        .message("댓글이 생성되었습니다.")
        .build());
  }

  @Operation(summary = "대댓글 생성", description = "대댓글 생성")
  @PostMapping("/{postId}/reply/{commentId}")
  public ResponseEntity<CommonResponse<List<CommentResponseDto>>> createReplyComment(
    @PathVariable Long postId,
    @PathVariable Long commentId,
    @AuthenticationPrincipal User user,
    @RequestBody CommentRequestDto requestDto) {
    commentService.createReplyComment(requestDto, postId, commentId, user);
    return ResponseEntity.status(HttpStatus.OK)
      .body(CommonResponse.<List<CommentResponseDto>>builder()
        .message("대댓글이 생성되었습니다.")
        .build());
  }

  @Operation(summary = "댓글 조회", description = "댓글 조회")
  @GetMapping("/{postId}")
  public ResponseEntity<CommonResponse<List<CommentResponseDto>>> getComment(
    @PathVariable Long postId) {
    List<CommentResponseDto> response = commentService.getComment(postId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(CommonResponse.<List<CommentResponseDto>>builder()
        .message("댓글이 조회되었습니다.")
        .data(response)
        .build());
  }

  @Operation(summary = "댓글 수정", description = "댓글 수정")
  @PutMapping("/{postId}/{commentId}")
  public ResponseEntity<CommonResponse<Void>> updateComment(
    @PathVariable Long commentId,
    @PathVariable Long postId,
    @AuthenticationPrincipal User user,
    @RequestBody CommentRequestDto requestDto) {
    commentService.updateComment(requestDto, commentId, postId, user);
    return ResponseEntity.status(HttpStatus.OK)
      .body(CommonResponse.<Void>builder()
        .message("댓글이 수정되었습니다.")
        .build());
  }

  @Operation(summary = "댓글 삭제", description = "댓글 삭제")
  @DeleteMapping("{commentId}")
  public ResponseEntity<CommonResponse<Void>> deleteComment(
    @PathVariable Long commentId
  ) {
    commentService.deleteComment(commentId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(CommonResponse.<Void>builder()
        .message("댓글이 삭제되었습니다.")
        .build());
  }


}
