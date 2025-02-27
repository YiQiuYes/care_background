package com.linping.care.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    // 设置 openapi 基础参数
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SpringBoot API 管理")
                        .contact(new Contact().name("林萍").email("3267125847@qq.com"))
                        .version("1.0")
                        .description("柳州市智慧养老系统Api文档")
                        .license(new License().name("Apache 2.0")));
    }
}
