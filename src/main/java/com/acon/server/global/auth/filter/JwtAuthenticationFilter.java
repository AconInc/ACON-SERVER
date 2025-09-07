package com.acon.server.global.auth.filter;

import static com.acon.server.global.auth.jwt.JwtValidationType.VALID_JWT;

import com.acon.server.global.auth.MemberAuthentication;
import com.acon.server.global.auth.jwt.JwtAuthenticationException;
import com.acon.server.global.auth.jwt.JwtTokenProvider;
import com.acon.server.global.auth.jwt.JwtValidationType;
import com.acon.server.global.exception.ErrorType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    // 각 HTTP 요청에 대해 토큰이 유효한지 확인하고, 유효하다면 해당 사용자를 인증 설정하는 필터링 로직
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            final String token = resolveToken(request);

            if (token == null) {
                filterChain.doFilter(request, response);

                return;
            }

            JwtValidationType result = jwtTokenProvider.validateToken(token);

            if (result == VALID_JWT) {
                Long memberId = jwtTokenProvider.getMemberIdFromJwt(token);

                MemberAuthentication authentication = new MemberAuthentication(memberId.toString(), null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                filterChain.doFilter(request, response);

                return;
            }

            throw mapToAuthException(result);

        } catch (JwtAuthenticationException ex) {
            authenticationEntryPoint.commence(request, response, ex);
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(authorization)) {
            return null;
        }

        if (authorization.equalsIgnoreCase("Bearer")) {
            throw new JwtAuthenticationException(ErrorType.INVALID_ACCESS_TOKEN_ERROR);
        }

        final String scheme = "Bearer ";

        if (!authorization.regionMatches(true, 0, scheme, 0, scheme.length())) {
            throw new JwtAuthenticationException(ErrorType.BEARER_LOST_ERROR);
        }

        String token = authorization.substring(scheme.length()).strip();

        if (!StringUtils.hasText(token)) {
            throw new JwtAuthenticationException(ErrorType.INVALID_ACCESS_TOKEN_ERROR);
        }

        return token;
    }

    private JwtAuthenticationException mapToAuthException(JwtValidationType v) {
        return switch (v) {
            case EXPIRED_JWT_TOKEN -> new JwtAuthenticationException(ErrorType.EXPIRED_ACCESS_TOKEN_ERROR);
            case INVALID_JWT_SIGNATURE, INVALID_JWT_TOKEN, UNSUPPORTED_JWT_TOKEN, EMPTY_JWT ->
                    new JwtAuthenticationException(ErrorType.INVALID_ACCESS_TOKEN_ERROR);
            case VALID_JWT -> throw new IllegalStateException("VALID_JWT must not reach mapToAuthException");
        };
    }
}
