package GaVisionUp.server.service.google;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import GaVisionUp.server.entity.User;
import GaVisionUp.server.service.user.UserQueryService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class GoogleService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleService.class);
    private static final String APPLICATION_NAME = "Google Sheets Application";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "/googlesheets/json";
    private Sheets sheetsService;
    private final UserQueryService userQueryService;

    private static final String SPREADSHEET_ID = "SPREADSHEET_ID"; // 스프레드시트 ID
    private static final String RANGE = "A1:D"; // 데이터를 읽고 쓰는 범위 (예시: A2부터 시작)

    public GoogleService(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    private Sheets getSheetsService() throws IOException, GeneralSecurityException {
        if (sheetsService == null) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(new ClassPathResource(CREDENTIALS_FILE_PATH).getInputStream())
                    .createScoped(Collections.singletonList("https://www.googleapis.com/auth/spreadsheets"));
            sheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        }
        return sheetsService;
    }

    /**
     * ✅ Google 스프레드시트에서 데이터 읽기
     */
    public List<List<Object>> readSheet() {
        try {
            Sheets service = getSheetsService();
            ValueRange response = service.spreadsheets().values()
                    .get(SPREADSHEET_ID, RANGE)
                    .execute();
            return response.getValues();
        } catch (Exception e) {
            logger.error("❌ Failed to read data from Google Sheets", e);
            return new ArrayList<>();
        }
    }

    /**
     * ✅ Google 스프레드시트 데이터 → DB 동기화
     */
    public void syncSheetToDatabase() {
        List<List<Object>> sheetData = readSheet();
        if (sheetData.isEmpty()) {
            logger.warn("⚠️ No data found in the Google Sheets.");
            return;
        }

        for (List<Object> row : sheetData) {
            if (row.size() < 2) continue; // 최소한 ID와 Name이 있어야 처리 가능

            Long userId = Long.parseLong(row.get(0).toString()); // 첫 번째 컬럼: User ID
            String userName = row.get(1).toString(); // 두 번째 컬럼: Name

            User user = userQueryService.getUserById(userId).orElse(null);
            if (user != null) {
                user.setName(userName);
                logger.info("🔄 Updated User {} from Google Sheets", userId);
            } else {
                logger.warn("⚠️ User ID {} not found in DB", userId);
            }
        }
    }

    /**
     * ✅ DB 데이터 → Google 스프레드시트 동기화
     */
    public void syncDatabaseToSheet() {
        List<User> users = userQueryService.getAllUsers();
        List<List<Object>> values = new ArrayList<>();

        for (User user : users) {
            List<Object> row = new ArrayList<>();
            row.add(user.getId());
            row.add(user.getName());
            values.add(row);
        }

        try {
            Sheets service = getSheetsService();
            ValueRange body = new ValueRange().setValues(values);
            UpdateValuesResponse result = service.spreadsheets().values()
                    .update(SPREADSHEET_ID, RANGE, body)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
            logger.info("✅ Successfully updated Google Sheets with DB data. Rows updated: {}", result.getUpdatedRows());
        } catch (Exception e) {
            logger.error("❌ Failed to write data to Google Sheets", e);
        }
    }
}
