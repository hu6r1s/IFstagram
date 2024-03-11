package com.nbcampif.ifstagram.domain.admin.service;

import com.nbcampif.ifstagram.domain.admin.dto.LoginRequestDto;
import com.nbcampif.ifstagram.domain.admin.dto.UserForceUpdateRequestDto;
import com.nbcampif.ifstagram.domain.post.dto.PostRequestDto;
import com.nbcampif.ifstagram.domain.post.dto.PostResponseDto;
import com.nbcampif.ifstagram.domain.user.dto.ReportReponseDto;
import com.nbcampif.ifstagram.domain.user.dto.UserResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface AdminService {

  void login(LoginRequestDto requestDto, HttpServletResponse response);
  UserResponseDto searchUser(Long userId);
  List<ReportReponseDto> searchReport(Long reportId);
  void updateUser(Long userId, UserForceUpdateRequestDto requestDto);
  void updatePost(Long postId, PostRequestDto requestDto, MultipartFile image) throws IOException;
  void deletePost(Long postId);
  List<PostResponseDto> getDeletedPost();
}
