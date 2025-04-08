package ru.panyukovnn.loggingstarter;

import feign.Logger;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import ru.panyukovnn.loggingstarter.aspect.LogExecutionAspect;
import ru.panyukovnn.loggingstarter.feign.FeignRequestLogger;
import ru.panyukovnn.loggingstarter.service.LoggingService;
import ru.panyukovnn.loggingstarter.webfilter.WebLoggingFilter;
import ru.panyukovnn.loggingstarter.webfilter.WebLoggingRequestBodyAdvice;

@AutoConfiguration
@ConditionalOnProperty(prefix = "logging", value = "enabled", havingValue = "true", matchIfMissing = true)
public class LoggingStarterAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "logging", value = "log-exec-time", havingValue = "true")
    public LogExecutionAspect logExecutionAspect() {
        return new LogExecutionAspect();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging.web-logging", value = "enabled", havingValue = "true", matchIfMissing = true)
    public WebLoggingFilter webLoggingFilter() {
        return new WebLoggingFilter();
    }

    @Bean
    @ConditionalOnBean(WebLoggingFilter.class)
    @ConditionalOnProperty(prefix = "logging.web-logging", value = "log-body", havingValue = "true")
    public WebLoggingRequestBodyAdvice webLoggingRequestBodyAdvice() {
        return new WebLoggingRequestBodyAdvice();
    }

    @Bean
    public LoggingService loggingService() {
        return new LoggingService();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging.web-logging", value = "log-feign-requests", havingValue = "true")
    public FeignRequestLogger feignRequestLogger() {
        return new FeignRequestLogger();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging.web-logging", value = "log-feign-requests", havingValue = "true")
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
