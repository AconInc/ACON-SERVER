package com.acon.server.global.logging;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@Order(1)
@Slf4j
public class RequestResponseLoggingFilter implements Filter {

    private static final String TRACE_ID = "traceId";
    private static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";

    @Override
    public void doFilter(
            final ServletRequest servletReq,
            final ServletResponse servletRes,
            final FilterChain filterChain
    ) throws IOException, ServletException {
        CachedBodyHttpServletRequest request = new CachedBodyHttpServletRequest((HttpServletRequest) servletReq);
        ContentCachingResponseWrapper response = new ContentCachingResponseWrapper((HttpServletResponse) servletRes);

        try {
            MDC.put(TRACE_ID, UUID.randomUUID().toString());

            String requestInfo = request.getMethod() + " | " + request.getRequestURI() + getRequestParams(request);
            String requestLog = "[Request]  " + requestInfo + " | " + getRequestAddr(request);
            String requestBody = request.getBody();

            if (!requestBody.isEmpty()) {
                requestLog += " | " + requestBody;
            }

            log.info(requestLog);

            filterChain.doFilter(request, response);

            String responseLog = "[Response] " + requestInfo + " | " + response.getStatus();
            String responseBody = getResponseBody(response);

            if (!responseBody.isEmpty()) {
                responseLog += " | " + responseBody;
            }

            log.info(responseLog);

            // 캐싱된 Response Body를 클라이언트에게 전달하기 위해 실제 응답 스트림으로 다시 복사
            response.copyBodyToResponse();
        } finally {
            MDC.clear();
        }
    }

    private String getRequestAddr(final HttpServletRequest request) {
        String requestAddr = request.getHeader(X_FORWARDED_FOR_HEADER);

        return (requestAddr != null) ? requestAddr : request.getRemoteAddr();
    }

    private String getRequestParams(final HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();

        if (parameterMap.isEmpty()) {
            return "";
        }

        String queryParameters = parameterMap.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue()[0])
                .collect(Collectors.joining("&"));

    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        // ContentCachingResponseWrapper에 캐싱된 Response Body를 가져와 UTF-8 문자열로 변환
        return new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
    }
}
