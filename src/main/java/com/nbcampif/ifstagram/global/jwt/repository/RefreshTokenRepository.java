package com.nbcampif.ifstagram.global.jwt.repository;

import com.nbcampif.ifstagram.global.jwt.RefreshTokenEntity;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityNotFoundException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

  private final RedisTemplate redisTemplate;

  @Resource(name = "redisTemplate")
  private ValueOperations<String, String> valueOperations;


  public void deleteToken(Long userId) {
    redisTemplate.delete(userId);
  }

  public void save(String userId, String refreshToken) {
    valueOperations.set(userId, refreshToken);
    redisTemplate.expire(userId, 7L, TimeUnit.DAYS);
  }

  public String findById(String userId) {
    String refreshToken = String.valueOf(valueOperations.get(userId));

    if (userId == null) {
      return null;
    }

    return refreshToken;
  }
}
