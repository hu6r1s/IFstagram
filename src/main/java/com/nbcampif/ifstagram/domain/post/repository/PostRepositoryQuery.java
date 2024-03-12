package com.nbcampif.ifstagram.domain.post.repository;

import com.nbcampif.ifstagram.domain.post.entity.Post;
import com.nbcampif.ifstagram.domain.post.entity.QPost;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepositoryQuery {

  private final JPAQueryFactory query;

  public List<Post> getCondPostList(String title) {
    QPost post = QPost.post;
    return query.select(post)
      .from(post)
      .where(post.title.eq(title))
      .fetch();
  }
}
