package GaVisionUp.server.web.controller.google;

import GaVisionUp.server.service.google.GoogleService;

import GaVisionUp.server.service.google.user.GoogleUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sheets")
@RequiredArgsConstructor
public class GoogleController {

    private final GoogleService googleService;
    private final GoogleUserService googleUserService;


    // ✅ Google 스프레드시트 → DB 동기화
    @PostMapping("/sync-to-db")
    public ResponseEntity<String> syncSheetToDatabase() {
        try {
            googleService.syncSheetToDatabase();
            return ResponseEntity.ok("✅ Google Sheets data synced to Database.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("❌ Failed to sync data: " + e.getMessage());
        }
    }

    // ✅ DB → Google 스프레드시트 동기화
    @PostMapping("/sync-to-sheet")
    public ResponseEntity<String> syncDatabaseToSheet() {
        try {
            googleService.syncDatabaseToSheet();
            return ResponseEntity.ok("✅ Database data synced to Google Sheets.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("❌ Failed to sync data: " + e.getMessage());
        }
    }

    /**
     * ✅ Google Sheets에서 유저 데이터 동기화 API
     */
    @PostMapping("/sync-users")
    public ResponseEntity<String> syncUsers() {
        googleUserService.syncUsersFromGoogleSheet();
        return ResponseEntity.ok("✅ Google Sheets에서 유저 데이터를 동기화했습니다.");
    }
}
