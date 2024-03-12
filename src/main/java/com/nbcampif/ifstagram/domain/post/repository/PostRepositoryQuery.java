package com.nbcampif.ifstagram.domain.post.repository;

import com.nbcampif.ifstagram.domain.post.entity.Post;
import com.nbcampif.ifstagram.domain.post.entity.QPost;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepositoryQuery {

  private final JPAQueryFactory query;

  public Page<Post> getCondPostList(String title, Pageable pageable) {
    QPost post = QPost.post;
    List<Post> fetch = query.select(post)
      .from(post)
      .where(post.title.eq(title))
      .offset(pageable.getOffset())
      .limit(pageable.getPageSize())
      .fetch();

    JPQLQuery<Post> count = query
      .select(post)
      .from(post)
      .where(post.title.eq(title));

    return PageableExecutionUtils.getPage(fetch, pageable, count::fetchCount);
  }
}
