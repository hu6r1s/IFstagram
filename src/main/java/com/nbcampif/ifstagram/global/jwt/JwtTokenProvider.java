package com.nbcampif.ifstagram.global.jwt;

import static com.nbcampif.ifstagram.global.jwt.TokenState.EXPIRED;
import static com.nbcampif.ifstagram.global.jwt.TokenState.INVALID;
import static com.nbcampif.ifstagram.global.jwt.TokenState.VALID;

import com.nbcampif.ifstagram.domain.user.model.User;
import com.nbcampif.ifstagram.domain.user.repository.UserRepository;
import com.nbcampif.ifstagram.global.jwt.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j(topic = "JWT 관련 로그")
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  private static final String AUTHORIZATION_ACCESS_TOKEN_HEADER_KEY = "Authorization";
  private static final String AUTHORIZATION_KEY = "Auth";
  private static final String BEARER_PREFIX = "Bearer ";
  private static final Integer BEARER_PREFIX_LENGTH = 7;
  private static final Long ACCESS_TOKEN_VALID_TIME = (60 * 1000L) * 30;
  private static final Long REFRESH_TOKEN_VALID_TIME = (60 * 1000L) * 60 * 24 * 7;
  private static final MacAlgorithm SIGNATURE_ALGORITHM = SIG.HS256;

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;

  @Value("${jwt.secret_key}")
  private String secretKey;
  private SecretKey key;

  @PostConstruct
  public void init() {
    byte[] bytes = Base64.getDecoder().decode(secretKey);
    key = Keys.hmacShaKeyFor(bytes);
  }

  public String generateAccessToken(
      final Long id, final String role
  ) {
    return generateToken(String.valueOf(id), role, ACCESS_TOKEN_VALID_TIME);
  }

  public String generateRefreshToken(
      Long userId, final String role
  ) {
    String token = generateToken(String.valueOf(userId), role, REFRESH_TOKEN_VALID_TIME);

    refreshTokenRepository.save(String.valueOf(userId), token);

    return token;
  }

  public String reGenerateAccessToken(String expiredToken) {
    log.info("[Re-generate access token]");
    User user = getUserFromToken(expiredToken);

    String refreshToken = refreshTokenRepository.findById(String.valueOf(user.getUserId()));
    TokenState state = checkTokenState(refreshToken);

    if (state.equals(INVALID)) {
      log.info("[Refresh token invalid] {}", refreshToken);
      throw new JwtException("Invalid Token");
    }
    if (state.equals(EXPIRED)) {
      log.info("[Refresh token expired] {}", refreshToken);
      refreshTokenRepository.deleteToken(user.getUserId());
      throw new JwtException("Expired Token");
    }

    return generateAccessToken(user.getUserId(), user.getRole().getAuthority());
  }

  private String generateToken(
      final String info, final String role, Long validTime
  ) {
    Date now = new Date();
    return BEARER_PREFIX + Jwts.builder()
        .subject(info)
        .claim(AUTHORIZATION_KEY, role)
        .expiration(new Date(now.getTime() + validTime))
        .issuedAt(now)
        .signWith(key, SIGNATURE_ALGORITHM)
        .compact();
  }

  public TokenState checkTokenState(final String token) {
    if (!StringUtils.hasText(token)) {
      return INVALID;
    }

    try {
      Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
      return VALID;
    } catch (SecurityException | MalformedJwtException | SignatureException e) {
      log.error("[Invalid JWT signature]", e);
      return INVALID;
    } catch (ExpiredJwtException e) {
      log.error("[Expired JWT token]", e);
      return EXPIRED;
    } catch (UnsupportedJwtException e) {
      log.error("[Unsupported JWT token]", e);
      return INVALID;
    } catch (IllegalArgumentException e) {
      log.error("[JWT claims is empty]", e);
      return INVALID;
    }
  }

  public String getAccessTokenFromRequest(final HttpServletRequest request) {
    return getTokenFromRequest(request, AUTHORIZATION_ACCESS_TOKEN_HEADER_KEY);
  }

  private String getTokenFromRequest(
      final HttpServletRequest request, final String headerField
  ) {
    Cookie[] cookies = request.getCookies();

    if (cookies == null) {
      throw new EntityNotFoundException("쿠키가 비어있습니다.");
    }

    Cookie findCookie = Arrays.stream(cookies)
        .filter(cookie -> cookie.getName().equals(headerField))
        .findFirst()
        .orElseThrow(() -> new EntityNotFoundException("토큰이 없습니다."));

    return URLDecoder.decode(findCookie.getValue(), StandardCharsets.UTF_8);
  }

  public void addAccessTokenToCookie(final String accessToken, final HttpServletResponse response) {
    addTokenToCookie(accessToken, AUTHORIZATION_ACCESS_TOKEN_HEADER_KEY, response);
  }

  private void addTokenToCookie(
      final String token, final String headerField, final HttpServletResponse response
  ) {
    String newToken = URLEncoder.encode(token, StandardCharsets.UTF_8).replace("\\+", "%20");

    Cookie cookie = new Cookie(headerField, newToken);
    cookie.setPath("/");

    response.addCookie(cookie);
  }

  public String substringToken(final String tokenValue) {
    if (!StringUtils.hasText(tokenValue) || !tokenValue.startsWith(BEARER_PREFIX)) {
      throw new JwtException("토큰이 비어있거나 Bearer로 시작하지 않습니다.");
    }

    return tokenValue.substring(BEARER_PREFIX_LENGTH);
  }

  public User getUserFromToken(String token) {
    Long userId = Long.valueOf(Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject());

    return userRepository.findUserOrElseThrow(userId);
  }

  @Transactional
  public void expireToken(
      final Long userId
  ) {
    refreshTokenRepository.deleteToken(userId);
  }

}
