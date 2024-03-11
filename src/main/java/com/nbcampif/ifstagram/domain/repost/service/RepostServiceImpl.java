package com.nbcampif.ifstagram.domain.repost.service;

import com.nbcampif.ifstagram.domain.post.entity.Post;
import com.nbcampif.ifstagram.domain.post.repository.PostRepository;
import com.nbcampif.ifstagram.domain.repost.entity.Repost;
import com.nbcampif.ifstagram.domain.repost.repository.RepostRepository;
import com.nbcampif.ifstagram.domain.user.model.User;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RepostServiceImpl implements RepostService{

  private final RepostRepository repostRepository;
  private final PostRepository postRepository;

  @Override
  @Transactional
  public void createRepost(
      Long postId, User user) {
    // 원본 게시글 정보 가져옴
    Post post = postRepository.findById(postId).orElseThrow(()
        -> new IllegalCallerException("일치하는 게시글이 없습니다."));

    Repost repost = new Repost(postId, user);

    repostRepository.save(repost);
  }
}

/*
    String uuid = UUID.randomUUID().toString();
    Post post = postRepository.findById(postId).orElseThrow(()
        -> new IllegalCallerException("일치하는 게시글이 없습니다."));

    // 이미지 경로 및 저장
    String fileName = image.getOriginalFilename();
    Path path = Paths.get(
        System.getProperty("user.home"), "Desktop", "IFstagram");
    Path filePath = path.resolve(uuid + fileName);
    String postImage = filePath + "";

    if (!Files.exists(path)) {
      Files.createDirectories(path);
    }

    Files.copy(image.getInputStream(), filePath);
 */
