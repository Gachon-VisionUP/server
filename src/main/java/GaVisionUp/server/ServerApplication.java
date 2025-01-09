package GaVisionUp.server;

import GaVisionUp.server.config.LogicConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Import(LogicConfig.class)
@SpringBootApplication
@ComponentScan(basePackages = "GaVisionUp.server") // ✅ 명시적으로 패키지 지정
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

}
