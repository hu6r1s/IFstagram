package com.nbcampif.ifstagram.domain.comment.dto;


import com.nbcampif.ifstagram.domain.comment.entity.Comment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CommentResponseDto {
    private Long postId;
    private Long commentId;
    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;
    private LocalDateTime deletedDate;
    private Long userId;
    private String content;
    private Long parentCommentId;

    public CommentResponseDto(Comment comment) {
        this.postId = comment.getPostId();
        this.commentId = comment.getId();
        this.userId = comment.getUserId();
        this.content = comment.getContent();
        this.parentCommentId = comment.getParentCommentId();
        this.createDate = comment.getCreatedAt();
        this.modifiedDate = comment.getModifiedAt();
        this.deletedDate = comment.getDeletedAt();
    }
}
