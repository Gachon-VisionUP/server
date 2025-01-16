package GaVisionUp.server.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Slf4j
@Configuration
public class GoogleSheetsConfig {

    private static final String APPLICATION_NAME = "Google Sheets Application"; // Google API 애플리케이션 이름
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    @Value("${google.sheets.credentials-file-path}")
    private String credentialsFilePath;

    /**
     * ✅ Google Sheets API의 `Sheets` 객체를 빈으로 등록
     */
    @Bean
    public Sheets sheetsService() throws IOException, GeneralSecurityException {
        log.info("📌 [INFO] Google Sheets API 인증 정보를 로드 중...");

        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new ClassPathResource(credentialsFilePath).getInputStream())
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/spreadsheets"));

        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
