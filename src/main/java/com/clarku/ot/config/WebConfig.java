package com.clarku.ot.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://online-testing-ui.vercel.app/", "https://release-1.d2cwvchoi4io6t.amplifyapp.com", "http://release-1.d2cwvchoi4io6t.amplifyapp.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
