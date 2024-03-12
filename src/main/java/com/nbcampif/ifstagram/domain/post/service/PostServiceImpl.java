package com.nbcampif.ifstagram.domain.post.service;

import com.nbcampif.ifstagram.domain.image.service.PostImageService;
import com.nbcampif.ifstagram.domain.post.dto.PostRequestDto;
import com.nbcampif.ifstagram.domain.post.dto.PostResponseDto;
import com.nbcampif.ifstagram.domain.post.entity.Post;
import com.nbcampif.ifstagram.domain.post.repository.PostRepository;
import com.nbcampif.ifstagram.domain.post.repository.PostRepositoryQuery;
import com.nbcampif.ifstagram.domain.user.model.User;
import com.nbcampif.ifstagram.domain.user.repository.FollowRepository;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{

  private final PostRepository postRepository;
  private final PostImageService postImageService;
  private final FollowRepository followRepository;
  private final PostRepositoryQuery postRepositoryQuery;

  @Override
  @Transactional
  public void createPost(
    PostRequestDto requestDto, MultipartFile image, User user) throws Exception {
    Post post = new Post(requestDto, user.getUserId());
    postRepository.save(post);

    postImageService.createImage(image, post);
  }

  @Override
  public List<PostResponseDto> getPostList() {
    return postRepository.findAll().stream().map(post -> {
        try {
          return new PostResponseDto(post, postImageService.getImage(post.getId()));
        } catch (MalformedURLException e) {
          throw new RuntimeException(e);
        }
      })
      .toList();
  }

  @Override
  public List<PostResponseDto> getCondPostList(String title) {
    return postRepositoryQuery.getCondPostList(title).stream().map(post -> {
      String imageUrl;
      try {
        imageUrl = postImageService.getImage(post.getId());
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
      return new PostResponseDto(post, imageUrl);
    })
    .toList();
  }

  @Override
  public PostResponseDto getPost(Long postId) throws MalformedURLException {
    Post post = findPost(postId);
    PostResponseDto responseDto = new PostResponseDto(
      post,
      postImageService.getImage(post.getId())
    );
    return responseDto;
  }

  @Override
  @Transactional
  public void updatePost(Long postId, PostRequestDto requestDto, MultipartFile image, User user)
    throws IOException {

    Post post = findPost(postId);
    if (!post.getUserId().equals(user.getUserId())) {
      throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
    }
    postImageService.updateImage(post, image);

    post.updatePost(requestDto);
  }

  @Override
  @Transactional
  public void deletePost(Long postId, User user) {
    Post post = findPost(postId);
    if (!post.getUserId().equals(user.getUserId())) {
      throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
    }

    post.delete();
  }

  public Post findPost(Long postId) {
    return postRepository.findById(postId).orElseThrow(()
      -> new IllegalCallerException("일치하는 게시글이 없습니다."));
  }

  public List<PostResponseDto> followPost(User user) {
    List<Long> userList = followRepository.findToUserIdByFromUserId(user.getUserId());
    List<Post> posts = postRepository.findAllByUserIdIn(userList);

    return posts.stream()
      .map(post -> {
        try {
          return new PostResponseDto(post, postImageService.getImage(post.getId()));
        } catch (MalformedURLException e) {
          throw new RuntimeException(e);
        }
      })
      .collect(Collectors.toList());
  }
}
