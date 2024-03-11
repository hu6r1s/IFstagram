package com.nbcampif.ifstagram.domain.report.service;

import com.nbcampif.ifstagram.domain.report.dto.ReportRequestDto;
import com.nbcampif.ifstagram.domain.report.dto.ReportResponseDto;
import com.nbcampif.ifstagram.domain.user.model.User;

public interface ReportService {

  ReportResponseDto reportUser(Long reportedUserId, User user, ReportRequestDto requestDto);
}
