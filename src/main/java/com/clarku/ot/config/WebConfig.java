package com.clarku.ot.config;

import java.io.IOException;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://online-testing-ui.vercel.app", 
                                "https://release-1.d2cwvchoi4io6t.amplifyapp.com", 
                                "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }

    // This filter logs CORS-related headers for every request
    @WebFilter("/*")
    public class CORSLoggingFilter implements Filter {

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            // Initialization if needed
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
                throws IOException, ServletException {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            // Log CORS-related headers
            String origin = httpRequest.getHeader("Origin");
            String method = httpRequest.getHeader("Access-Control-Request-Method");
            String headers = httpRequest.getHeader("Access-Control-Request-Headers");

            if (origin != null || method != null || headers != null) {
                log.info("CORS Request Detected:");
                log.info("Origin: " + origin);
                log.info("Access-Control-Request-Method: " + method);
                log.info("Access-Control-Request-Headers: " + headers);
            }

            // Continue with the request processing
            chain.doFilter(request, response);
        }

        @Override
        public void destroy() {
            // Cleanup if necessary
        }
    }

    // Register the CORS logging filter to log CORS headers
    @Bean
    public FilterRegistrationBean<CORSLoggingFilter> loggingFilter() {
        FilterRegistrationBean<CORSLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CORSLoggingFilter());
        registrationBean.addUrlPatterns("/**"); // or specify your specific URL pattern
        return registrationBean;
    }
}
