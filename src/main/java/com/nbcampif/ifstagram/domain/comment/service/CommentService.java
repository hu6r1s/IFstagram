package com.nbcampif.ifstagram.domain.comment.service;

import com.nbcampif.ifstagram.domain.comment.dto.CommentRequestDto;
import com.nbcampif.ifstagram.domain.comment.dto.CommentResponseDto;
import com.nbcampif.ifstagram.domain.comment.entity.Comment;
import com.nbcampif.ifstagram.domain.comment.repository.CommentRepository;
import com.nbcampif.ifstagram.domain.post.service.PostService;
import com.nbcampif.ifstagram.domain.user.model.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final PostService postService;

  @Transactional
  public void createComment(CommentRequestDto requestDto, Long postId, User user) {
    postService.findPost(postId);
    Comment comment = new Comment(requestDto, postId, user);
    commentRepository.save(comment);
  }

  @Transactional
  public void createReplyComment(
    CommentRequestDto requestDto, Long postId, Long commentId, User user) {
    postService.findPost(postId);
    findComment(commentId);
    Comment comment = new Comment(commentId, requestDto, postId, user);
    commentRepository.save(comment);
  }

  public List<CommentResponseDto> getComment(Long postId) {
    postService.findPost(postId);
    List<Comment> comments = commentRepository.findAllByPostId(postId);

    return comments.stream().map(CommentResponseDto::new).toList();
  }

  @Transactional
  public void updateComment(CommentRequestDto requestDto, Long commentId, Long postId, User user) {
    Comment comment = findComment(commentId);
    if (!comment.getUserId().equals(user.getUserId())) {
      throw new IllegalArgumentException("자신이 작성한 댓글만 수정할 수 있습니다.");
    }
    comment.update(requestDto.getContent());
  }

  @Transactional
  public void deleteComment(Long commentId, User user) {
    Comment comment = findComment(commentId);
    if (!comment.getUserId().equals(user.getUserId())) {
      throw new IllegalArgumentException("자신이 작성한 댓글만 수정할 수 있습니다.");
    }
    comment.delete();
  }

  private Comment findComment(Long commentId) {
    return commentRepository.findById(commentId)
      .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));
  }
}
