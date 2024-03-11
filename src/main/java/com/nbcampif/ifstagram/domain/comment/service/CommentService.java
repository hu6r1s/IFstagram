package com.nbcampif.ifstagram.domain.comment.service;

import com.nbcampif.ifstagram.domain.comment.dto.CommentRequestDto;
import com.nbcampif.ifstagram.domain.comment.dto.CommentResponseDto;
import com.nbcampif.ifstagram.domain.comment.entity.Comment;
import com.nbcampif.ifstagram.domain.user.model.User;
import java.util.List;

public interface CommentService {

  void createComment(CommentRequestDto requestDto, Long postId, User user);
  void createReplyComment(CommentRequestDto requestDto, Long postId, Long commentId, User user);
  List<CommentResponseDto> getComment(Long postId);
  void updateComment(CommentRequestDto requestDto, Long commentId, Long postId, User user);
  void deleteComment(Long commentId, User user);
}
