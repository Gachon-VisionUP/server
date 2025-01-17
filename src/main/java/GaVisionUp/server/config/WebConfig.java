package GaVisionUp.server.config;

import GaVisionUp.server.converter.FilterConverter;
import GaVisionUp.server.converter.JobGroupConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://35.216.61.56:8080", "http://35.216.61.56:8081")
                .allowedOrigins("http://localhost:8080", "http://localhost:8081")
                .allowedOrigins("http://35.216.61.56:3000", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");

        /*
        * registry.addMapping("/**")
                .allowedOrigins(
                    "http://35.216.61.56:8080",
                    "http://35.216.61.56:8081",
                    "http://localhost:8080",
                    "http://localhost:8081",
                    "http://35.216.61.56:3000",
                    "http://localhost:3000"
                )
                .allowedHeaders("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true);
        * */
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new FilterConverter());
        registry.addConverter(new JobGroupConverter());
    }
}