package com.nbcampif.ifstagram.global.jwt;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash
public class RefreshTokenEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long refreshTokenId;
  private Long userId;
  private String token;

  private RefreshTokenEntity(Long userId, String token) {
    this.userId = userId;
    this.token = token;
  }

  public static RefreshTokenEntity of(Long memberId, String token) {
    return new RefreshTokenEntity(memberId, token);
  }

}
