package ru.itmo.park.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("Authorization, Content-Type, X-Total-Count, Content-Disposition")
                .exposedHeaders("Authorization, Content-Type, X-Total-Count, Content-Disposition");
        //registry.addMapping("/api/**").allowedOrigins("*").allowedMethods("*").allowedHeaders("Authorization,");
    }
}
