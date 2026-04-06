package com.studentreview.server.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Permitem cereri de la orice origine
        // Pentru producție, ar trebui să restricționezi la domeniul tău
        registry.addMapping("/api/**") // Se aplică doar la rutele care încep cu /api/
                .allowedOrigins("*") // Permite orice origine
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Permite aceste metode
                .allowedHeaders("*"); // Permite orice antet
    }
}