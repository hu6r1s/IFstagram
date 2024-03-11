package com.nbcampif.ifstagram.domain.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "api_use_time")
public class ApiUseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(nullable = false)
  private Long totalTime;

  public ApiUseTime(Long userId, Long totalTime) {
    this.userId = userId;
    this.totalTime = totalTime;
  }

  public void addUseTime(long useTime) {
    this.totalTime += useTime;
  }
}
