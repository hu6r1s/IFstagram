package com.nbcampif.ifstagram.domain.post.repository;

import com.nbcampif.ifstagram.domain.post.entity.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

  @Modifying
  @Query("update Post p set p.likeCount = p.likeCount + 1 WHERE p.id = :postId")
  void upCount(@Param("postId") Long postId);

  @Modifying
  @Query("update Post p set p.likeCount = p.likeCount - 1 WHERE p.id = :postId")
  void downCount(@Param("postId") Long postId);

  List<Post> findAllByDeletedAtIsNotNullOrderByDeletedAtDesc(Pageable pageable);

  List<Post> findAllByUserIdIn(List<Long> userList);
}
