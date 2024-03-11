package com.nbcampif.ifstagram.domain.admin.repository;

import com.nbcampif.ifstagram.domain.admin.entity.ApiUseTime;
import com.nbcampif.ifstagram.domain.user.dto.UserResponseDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiUseTimeRepository extends JpaRepository<ApiUseTime, Long> {

  Optional<ApiUseTime> findByUserId(Long userId);

  List<ApiUseTime> findUserIdByOrderByTotalTimeDesc();

}
