package ru.panyukovnn.loggingstarter.service;

import feign.Request;
import feign.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.panyukovnn.loggingstarter.dto.RequestDirection;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoggingService {

    @Value("${logging.web-logging.log-body:true}")
    private boolean logBody;

    private static final Logger log = LoggerFactory.getLogger(LoggingService.class);

    public void logRequest(HttpServletRequest request) {
        String method = request.getMethod();
        String requestURI = request.getRequestURI() + formatQueryString(request);
        String headers = inlineHeaders(request);

        log.info("Запрос: {} {} {} {}", RequestDirection.IN, method, requestURI, headers);
    }

    public void logFeignRequest(Request request) {
        String method = request.httpMethod().name();
        String requestURI = request.url();
        String headers = inlineHeaders(request.headers());
        String body = logBody ? new String(request.body(), StandardCharsets.UTF_8) : "";

        log.info("Запрос: {} {} {} {} body={}", RequestDirection.OUT, method, requestURI, headers, body);
    }

    public void logRequestBody(HttpServletRequest request, Object body) {
        String method = request.getMethod();
        String requestURI = request.getRequestURI() + formatQueryString(request);

        Object bodyToLog = logBody ? body : "";

        log.info("Тело запроса: {} {} {} {}", RequestDirection.IN, method, requestURI, bodyToLog);
    }

    public void logResponse(HttpServletRequest request, HttpServletResponse response, String responseBody) {
        String method = request.getMethod();
        String requestURI = request.getRequestURI() + formatQueryString(request);

        Object bodyToLog = logBody ? responseBody : "";

        log.info("Ответ: {} {} {} {} body={}", RequestDirection.IN, method, requestURI, response.getStatus(), bodyToLog);
    }

    public void logFeignResponse(Response response, String responseBody) {
        String url = response.request().url();
        String method = response.request().httpMethod().name();
        int status = response.status();

        Object bodyToLog = logBody ? responseBody : "";

        log.info("Ответ: {} {} {} {} body={}", RequestDirection.OUT, method, url, status, bodyToLog);
    }

    private String inlineHeaders(HttpServletRequest request) {
        Map<String, Collection<String>> headersMap = Collections.list(request.getHeaderNames()).stream()
            .collect(Collectors.toMap(it -> it, headerName -> Collections.list(request.getHeaders(headerName))));

        return inlineHeaders(headersMap);
    }

    private String inlineHeaders(Map<String, Collection<String>> headersMap) {
        String inlineHeaders = headersMap.entrySet().stream()
            .map(entry -> {
                String headerName = entry.getKey();
                String headerValue = String.join(",", entry.getValue());

                return headerName + "=" + headerValue;
            })
            .collect(Collectors.joining(","));

        return "headers={" + inlineHeaders + "}";
    }

    private String formatQueryString(HttpServletRequest request) {
        return Optional.ofNullable(request.getQueryString())
            .map(qs -> "?" + qs)
            .orElse(Strings.EMPTY);
    }
}
