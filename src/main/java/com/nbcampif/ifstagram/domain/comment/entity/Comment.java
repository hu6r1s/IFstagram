package com.nbcampif.ifstagram.domain.comment.entity;


import com.nbcampif.ifstagram.domain.comment.dto.CommentRequestDto;
import com.nbcampif.ifstagram.domain.user.model.User;
import com.nbcampif.ifstagram.global.entity.Timestamped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor
@Table(name = "comment")
public class Comment extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private Long postId;

  @Column
  private Long parentCommentId;



  public Comment(CommentRequestDto requestDto, Long postId, User user) {
    this.content = requestDto.getContent();
    this.postId = postId;
    this.userId = user.getUserId();
  }

  public Comment(Long parentCommentId, CommentRequestDto requestDto, Long postId, User user) {
    this.parentCommentId = parentCommentId;
    this.content = requestDto.getContent();
    this.postId = postId;
    this.userId = user.getUserId();
  }

  public void delete() {
    super.deletedAt = LocalDateTime.now();
  }

  public void update(String content) {
    this.content = content;
  }
}
