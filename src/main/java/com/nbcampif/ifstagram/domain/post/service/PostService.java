package com.nbcampif.ifstagram.domain.post.service;

import com.nbcampif.ifstagram.domain.post.dto.PostRequestDto;
import com.nbcampif.ifstagram.domain.post.dto.PostResponseDto;
import com.nbcampif.ifstagram.domain.post.entity.Post;
import com.nbcampif.ifstagram.domain.user.model.User;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {

  void createPost(PostRequestDto requestDto, MultipartFile image, User user) throws Exception;
  List<PostResponseDto> getPostList();
  PostResponseDto getPost(Long postId) throws MalformedURLException;
  void updatePost(Long postId, PostRequestDto requestDto, MultipartFile image, User user)
    throws IOException;
  void deletePost(Long postId, User user);
  Post findPost(Long postId);
  List<PostResponseDto> followPost(User user);
}
