package com.acon.server.global.auth.jwt;

import static org.apache.commons.codec.binary.Base64.decodeBase64;

import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String MEMBER_ID = "memberId";

    private final CacheManager cacheManager;

    @Value("${jwt.access-token-expire-time}")
    private long ACCESS_TOKEN_EXPIRATION_TIME;

    @Value("${jwt.refresh-token-expire-time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME;

    @Value("${jwt.secret}")
    private String JWT_SECRET;

    @PostConstruct
    protected void init() {
        //base64 라이브러리에서 encodeToString을 이용해서 byte[] 형식을 String 형식으로 변환
        JWT_SECRET = Base64.getEncoder().encodeToString(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String issueAccessToken(final Authentication authentication) {
        return generateToken(authentication, ACCESS_TOKEN_EXPIRATION_TIME);
    }

    public String issueRefreshToken(Long memberId) {
        final Date now = new Date();

        final String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
        storeRefreshToken(refreshToken, memberId);

        return refreshToken;
    }

    public String generateToken(Authentication authentication, Long tokenExpirationTime) {
        final Date now = new Date();

        final Claims claims = Jwts.claims()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenExpirationTime)); // 만료 시간 설정

        claims.put(MEMBER_ID, authentication.getPrincipal());

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // Header
                .setClaims(claims) // Claim
                .signWith(getSigningKey()) // Signature
                .compact();
    }

    private SecretKey getSigningKey() {
        String encodedKey = Base64.getEncoder().encodeToString(JWT_SECRET.getBytes()); // SecretKey를 통해 서명 생성

        // 일반적으로 HMAC (Hash-based Message Authentication Code) 알고리즘을 사용
        return Keys.hmacShaKeyFor(encodedKey.getBytes());
    }

    public JwtValidationType validateToken(String token) {
        try {
            final Claims claims = getBody(token);
            return JwtValidationType.VALID_JWT;
        } catch (MalformedJwtException ex) {
            return JwtValidationType.INVALID_JWT_TOKEN;
        } catch (ExpiredJwtException ex) {
            return JwtValidationType.EXPIRED_JWT_TOKEN;
        } catch (UnsupportedJwtException ex) {
            return JwtValidationType.UNSUPPORTED_JWT_TOKEN;
        } catch (IllegalArgumentException ex) {
            return JwtValidationType.EMPTY_JWT;
        }
    }

    private Claims getBody(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getMemberIdFromJwt(String token) {
        Claims claims = getBody(token);

        return Long.valueOf(claims.get(MEMBER_ID).toString());
    }

    public Map<String, String> parseHeaders(String token) {
        String header = token.split("\\.")[0];

        try {
            return new ObjectMapper().readValue(decodeBase64(header), Map.class);
        } catch (IOException e) {
            throw new BusinessException(ErrorType.INTERNAL_SERVER_ERROR);
        }
    }

    public Claims getTokenClaims(String token, PublicKey publicKey) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // TODO: cache token 관리 로직 클래스로 분리
    public void storeRefreshToken(String refreshToken, Long memberId) {
        Cache cache = cacheManager.getCache("refreshTokenCache");
        // 존재 유무만 확인하므로 빈 값
        // TODO: Refresh Token key 수정
        cache.put(refreshToken, memberId);
    }

    public Long validateRefreshToken(String refreshToken) {
        Cache cache = cacheManager.getCache("refreshTokenCache");
        // Intellij는 해당 조건이 항상 true라고 메세지를 띄우지만 cache.get(memberId)의 반환값이 존재하지 않을 시 null입니다!
        if (cache.get(refreshToken) != null) {
            return cache.get(refreshToken, Long.class);
        }
        throw new BusinessException(ErrorType.INVALID_REFRESH_TOKEN_ERROR);
    }

    // TODO: 블랙리스트 설정 필요
    public void deleteRefreshToken(String refreshToken) {
        Cache cache = cacheManager.getCache("refreshTokenCache");
        validateRefreshToken(refreshToken);
        cache.evict(refreshToken);
    }
}
