package com.nbcampif.ifstagram.domain.admin.service;

import com.nbcampif.ifstagram.domain.admin.dto.LoginRequestDto;
import com.nbcampif.ifstagram.domain.admin.dto.UserForceUpdateRequestDto;
import com.nbcampif.ifstagram.domain.admin.entity.ApiUseTime;
import com.nbcampif.ifstagram.domain.admin.repository.ApiUseTimeRepository;
import com.nbcampif.ifstagram.domain.image.service.PostImageService;
import com.nbcampif.ifstagram.domain.post.dto.PostRequestDto;
import com.nbcampif.ifstagram.domain.post.dto.PostResponseDto;
import com.nbcampif.ifstagram.domain.post.entity.Post;
import com.nbcampif.ifstagram.domain.post.repository.PostRepository;
import com.nbcampif.ifstagram.domain.report.Entity.Report;
import com.nbcampif.ifstagram.domain.report.repository.ReportRepository;
import com.nbcampif.ifstagram.domain.user.UserRole;
import com.nbcampif.ifstagram.domain.user.dto.ReportReponseDto;
import com.nbcampif.ifstagram.domain.user.dto.UserResponseDto;
import com.nbcampif.ifstagram.domain.user.model.User;
import com.nbcampif.ifstagram.domain.user.repository.UserRepository;
import com.nbcampif.ifstagram.global.exception.NotFoundUserException;
import com.nbcampif.ifstagram.global.exception.PermissionNotException;
import com.nbcampif.ifstagram.global.jwt.JwtTokenProvider;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

  private final UserRepository userRepository;
  private final PostRepository postRepository;
  private final ApiUseTimeRepository apiUseTimeRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final PostImageService postImageService;
  private final ReportRepository reportRepository;

  @Value("${admin.email}")
  private String adminEmail;

  @Value("${admin.password}")
  private String verifyPassword;

  @PostConstruct
  public void createAdminAccount() {
    boolean existsAdmin = userRepository.existsByRole(UserRole.ADMIN);
    if (!existsAdmin) {
      User admin = new User(1L, adminEmail, "admin", UUID.randomUUID()
          .toString(), null, UserRole.ADMIN);
      userRepository.createUser(admin);
    }
  }

  @Override
  public void login(LoginRequestDto requestDto, HttpServletResponse response) {
    String password = requestDto.getPassword();
    User user = userRepository.findByEmail(requestDto.getEmail())
        .orElseThrow(() -> new NotFoundUserException("해당 유저는 존재하지 않습니다."));
    if (!verifyPassword.equals(password) || !user.getRole().equals(UserRole.ADMIN)) {
      throw new PermissionNotException("허용되지 않은 권한입니다.");
    }

    String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId(),
        user.getRole().getAuthority());
    String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId(),
        user.getRole().getAuthority());

    jwtTokenProvider.addAccessTokenToCookie(accessToken, response);
  }

  @Override
  public UserResponseDto searchUser(Long userId) {
    User user = userRepository.findUser(userId)
        .orElseThrow(() -> new NotFoundUserException("해당 유저는 존재하지 않습니다."));

    UserResponseDto responseDto = UserResponseDto.of(user);
    return responseDto;
  }

  @Override
  public List<ReportReponseDto> searchReport(Long reportId) {
    List<Report> reportList = reportRepository.findAllByToUserId(reportId);
    List<ReportReponseDto> responseDtoList = reportList.stream()
        .map(report -> new ReportReponseDto(
            report.getContent(), report.getFromUserId(), report.getToUserId())
        )
        .collect(Collectors.toList());

    return responseDtoList;
  }

  @Override
  @Transactional
  public void updateUser(Long userId, UserForceUpdateRequestDto requestDto) {
    User user = userRepository.findUser(userId)
        .orElseThrow(() -> new NotFoundUserException("해당 유저는 존재하지 않습니다."));

    userRepository.updateUser(requestDto, user);
  }

  @Override
  @Transactional
  public void updatePost(Long postId, PostRequestDto requestDto, MultipartFile image)
      throws IOException {

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
    postImageService.updateImage(post, image);
    post.updatePost(requestDto);
  }

  @Override
  @Transactional
  public void deletePost(Long postId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다.")
        );
    post.delete();
  }

  @Override
  public List<PostResponseDto> getDeletedPost() {
    return postRepository.findAllByDeletedAtIsNotNullOrderByDeletedAtDesc()
        .stream().map(e -> {
          String imageUrl;
          try {
            imageUrl = postImageService.getImage(e.getId());
          } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
          }
          return new PostResponseDto(e, imageUrl);
        })
        .toList();
  }

  @Override
  public List<UserResponseDto> getEvent() {
    List<UserResponseDto> responseList = new ArrayList<>();
    List<ApiUseTime> userList = apiUseTimeRepository.findUserIdByOrderByTotalTimeDesc();
    for (ApiUseTime apiUseTime : userList) {
      User user = userRepository.findUserOrElseThrow(apiUseTime.getUserId());
      responseList.add(new UserResponseDto(
        user.getEmail(),
        user.getIntroduction(),
        user.getNickname(),
        user.getProfileImage(),
        user.getReportedCount()
        )
      );
    }
    return responseList;
  }

}
