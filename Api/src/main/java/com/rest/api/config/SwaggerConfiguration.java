package com.rest.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    @Bean
    public Docket swaggerApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(swaggerInfo()).select()
                .apis(RequestHandlerSelectors.basePackage("com.rest.api.controller"))
                .paths(PathSelectors.ant("/v1/**"))
                .build()
                .useDefaultResponseMessages(false);
        // 기본적으로 세팅되는 http 리턴값 200, 401, 403, 404 에 대한 메세지 표시
                // .paths(PathSelectors.any())
    }

    private ApiInfo swaggerInfo() {
        return new ApiInfoBuilder()
                .title("Spring Boot API Document")
                .description("앱 개발에 사용되는 서버 API 문서")
                .license("Copy right, tibty,. ltd").licenseUrl("tibty.vn")
                .version("0.0.1")
                .build();
    }
}
