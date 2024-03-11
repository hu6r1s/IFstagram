package com.nbcampif.ifstagram.domain.report.service;


import com.nbcampif.ifstagram.domain.report.Entity.Report;
import com.nbcampif.ifstagram.domain.report.dto.ReportRequestDto;
import com.nbcampif.ifstagram.domain.report.dto.ReportResponseDto;
import com.nbcampif.ifstagram.domain.report.repository.ReportRepository;
import com.nbcampif.ifstagram.domain.user.model.User;
import com.nbcampif.ifstagram.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

  private final UserRepository userRepository;
  private final ReportRepository reportRepository;

  @Override
  @Transactional
  public ReportResponseDto reportUser(
    Long reportedUserId, User user, ReportRequestDto requestDto
  ) {
    User reportedUser = findUser(reportedUserId);
    userRepository.updateReportedCount(reportedUser);
    reportRepository.save(new Report(reportedUserId, user.getUserId(), requestDto.getContent()));
    String reportResult = String.format(
      "%s이 %s를 신고하셨습니다. 사유 : %s",
      user.getName(),
      reportedUser.getName(),
      requestDto.getContent()
    );
    ReportResponseDto response = new ReportResponseDto(reportResult);
    return response;
  }

  private User findUser(Long id) {
    return userRepository.findUser(id).orElseThrow(() ->
      new IllegalArgumentException("신고할 사용자가 존재하지 않습니다.")
    );
  }

}
