package com.clarku.ot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
	@Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Online Testing API")
                        .version("1.0")
                        .description("API documentation for the Online Testing application"))
	        .addSecurityItem(new SecurityRequirement().addList("sessionId"))
	        .components(new io.swagger.v3.oas.models.Components()
	                .addSecuritySchemes("sessionId",
	                    new SecurityScheme()
	                        .name("sessionId")
	                        .type(SecurityScheme.Type.APIKEY)
	                        .in(SecurityScheme.In.HEADER)
	                        .description("Session ID for authentication")));
	}
}
