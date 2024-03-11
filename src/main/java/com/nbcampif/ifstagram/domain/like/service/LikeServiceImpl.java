package com.nbcampif.ifstagram.domain.like.service;

import com.nbcampif.ifstagram.domain.like.entity.Like;
import com.nbcampif.ifstagram.domain.like.repository.LikeRepository;
import com.nbcampif.ifstagram.domain.post.entity.Post;
import com.nbcampif.ifstagram.domain.post.repository.PostRepository;
import com.nbcampif.ifstagram.domain.user.model.User;
import com.nbcampif.ifstagram.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService{

  private final PostRepository postRepository;
  private final LikeRepository likeRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public Long countLike(Long postId, User user) {
    // todo: like 테이블에서 post_id 카운팅 해서 하는 방법도 있지만
    //  데이터가 많아지면 카운팅 하는데 문제가 생길 수 있다.
    // postID로 postRepository조회해서 해당 게시글 정보 가져오기
    // 게시글 조회
    Post postSeq = postRepository.findById(postId).orElseThrow(()
        -> new IllegalCallerException("일치하는 게시글이 없습니다."));
    // 로그인 유저 조회
    User userInfo = userRepository.findUser(user.getUserId()).orElseThrow(()
        -> new IllegalCallerException("일치하는 유저 없음"));
    Like likeInfo = likeRepository.findByUserIdAndPostId(userInfo.getUserId(), postSeq.getId());

    // 사용자 확인: 사용자 아이디로 조회해서 해당 postId글에 좋아요했는지 조회
    Long count = 0L;
    if (likeInfo == null) {
      // likeRepository에 postId, userId를 저장
      Like like = new Like(userInfo.getUserId(), postId);
      likeRepository.save(like);
      // count 수를 올려서 수정
      postRepository.upCount(postId);
      count = postRepository.findById(postId).get().getLikeCount() + 1;
    } else {
      likeRepository.delete(likeInfo);
      postRepository.downCount(postId);
      count = postRepository.findById(postId).get().getLikeCount() - 1;
    }
    // 이 사용자가 해당 postId에 좋아요를 하지 않았다면 아래가 수행됨

    return count;
  }
}
