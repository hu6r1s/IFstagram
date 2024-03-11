package com.nbcampif.ifstagram.domain.user.service;

import com.nbcampif.ifstagram.domain.user.dto.UserUpdateRequestDto;
import com.nbcampif.ifstagram.domain.user.model.User;
import com.nbcampif.ifstagram.global.response.CommonResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {

  ResponseEntity<CommonResponse<Void>> reportUser(Long userId);
  void follow(User user, Long toUserId);
  User updateUser(UserUpdateRequestDto requestDto, User user);
}
