package GaVisionUp.server.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EnvironmentCheck {
    private final Environment env;

    public EnvironmentCheck(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void init(){
        String url = env.getProperty("url");
        String name = env.getProperty("name");
        log.info("url = {}", url);
        log.info("name = {}", name);
    }
}