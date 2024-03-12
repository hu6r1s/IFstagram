package com.nbcampif.ifstagram.domain.comment.repository;

import com.nbcampif.ifstagram.domain.comment.entity.Comment;
import com.nbcampif.ifstagram.domain.comment.entity.QComment;
import com.nbcampif.ifstagram.domain.post.entity.QPost;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryQuery {

  private final JPAQueryFactory query;

  public List<Comment> getComments(Long postId) {
    QComment comment = QComment.comment;
    QPost post = QPost.post;

    return query.select(comment)
      .from(comment)
      .leftJoin(post)
      .on(comment.postId.eq(post.id))
      .where(comment.postId.eq(postId))
      .fetch();
  }
}
