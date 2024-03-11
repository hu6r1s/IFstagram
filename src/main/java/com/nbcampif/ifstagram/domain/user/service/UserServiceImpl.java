package com.nbcampif.ifstagram.domain.user.service;

import com.nbcampif.ifstagram.domain.user.dto.UserUpdateRequestDto;
import com.nbcampif.ifstagram.domain.user.model.Follow;
import com.nbcampif.ifstagram.domain.user.model.User;
import com.nbcampif.ifstagram.domain.user.repository.FollowRepository;
import com.nbcampif.ifstagram.domain.user.repository.RecentPasswordRepository;
import com.nbcampif.ifstagram.domain.user.repository.UserRepository;
import com.nbcampif.ifstagram.domain.user.repository.entity.RecentPassword;
import com.nbcampif.ifstagram.global.exception.RecentPasswordException;
import com.nbcampif.ifstagram.global.response.CommonResponse;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Transactional
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

  private final UserRepository userRepository;
  private final FollowRepository followRepository;
  private final RecentPasswordRepository recentPasswordRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public ResponseEntity<CommonResponse<Void>> reportUser(Long userId) {
    User reportedUser = findUserById(userId);
    userRepository.updateReportedCount(reportedUser);

    return CommonResponse.ok("유저가 신고되었습니다", null);
  }

  public User findUserById(Long id) {
    return userRepository.findUser(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
  }

  @Override
  public void follow(User user, Long toUserId) {
    Long fromUserId = user.getUserId();

    if (Objects.equals(fromUserId, toUserId)) {
      throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
    }

    Optional<Follow> savedFollow = followRepository.findByFromUserIdAndToUserId(fromUserId, toUserId);
    if (savedFollow.isEmpty()) {
      followRepository.saveFollow(fromUserId, toUserId);
    } else {
      followRepository.deleteFollow(savedFollow.get());
    }
  }

  @Override
  public User updateUser(UserUpdateRequestDto requestDto, User user) {
    User savedUser = userRepository.findByEmailOrElseThrow(user.getEmail());

    if (!isPasswordMatching(requestDto.getPassword(), requestDto.getConfirmPassword())) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    validateRecentPasswords(requestDto.getPassword(), user.getUserId());

    saveRecentPassword(requestDto.getPassword(), user.getUserId());

    return userRepository.updateUser(requestDto, savedUser);
  }

  private boolean isPasswordMatching(String password, String confirmPassword) {
    return StringUtils.hasText(password) || password.equals(confirmPassword);
  }

  private void validateRecentPasswords(String newPassword, Long userId) {
    List<RecentPassword> recentPasswords = recentPasswordRepository
        .findTop3RecentPasswordsByUserIdOrderByCreatedAtDesc(userId);

    for (RecentPassword recentPassword : recentPasswords) {
      if (passwordEncoder.matches(newPassword, recentPassword.getPassword())) {
        throw new RecentPasswordException("입력한 비밀번호가 최근에 사용된 비밀번호입니다.");
      }
    }
  }

  private void saveRecentPassword(String password, Long userId) {
    if (!StringUtils.hasText(password)) {
      RecentPassword recentPassword = new RecentPassword(password, userId);
      recentPasswordRepository.save(recentPassword);
    }
  }

}
