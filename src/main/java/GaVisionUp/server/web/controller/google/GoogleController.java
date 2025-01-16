package GaVisionUp.server.web.controller.google;

import GaVisionUp.server.service.google.job.GoogleJobQuestDetailService;
import GaVisionUp.server.service.google.GooglePerformanceService;
import GaVisionUp.server.service.google.GoogleUserService;
import GaVisionUp.server.service.google.job.GoogleJobQuestService;
import GaVisionUp.server.service.google.leader.GoogleLeaderQuestConditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sheets")
@RequiredArgsConstructor
public class GoogleController {

    private final GoogleUserService googleUserService;
    private final GooglePerformanceService googlePerformanceService;
    private final GoogleJobQuestDetailService googleJobQuestDetailService;
    private final GoogleJobQuestService googleJobQuestService;
    private final GoogleLeaderQuestConditionService googleLeaderQuestConditionService;

    /**
     * ✅ Google Sheets에서 유저 데이터 동기화 API
     */
    @PostMapping("/sync-sheet-to-db")
    public ResponseEntity<String> syncSheetToDb() {
        googleUserService.syncUsersFromGoogleSheet();
        googlePerformanceService.syncH1PerformanceFromGoogleSheet();
        googlePerformanceService.syncH2PerformanceFromGoogleSheet();
        googleJobQuestDetailService.syncJobQuestDetailFromGoogleSheet();
        googleJobQuestService.syncJobQuestFromGoogleSheet();
        googleLeaderQuestConditionService.syncLeaderQuestConditions();
        return ResponseEntity.ok("✅ Google Sheets에서 DB에 데이터를 동기화했습니다.");
    }

    /**
     * ✅ DB → Google Sheets 동기화 (DB → Google Sheets)
     */
    @PostMapping("/sync-db-to-sheets")
    public ResponseEntity<String> syncDbToGoogleSheets() {
        googleUserService.syncDatabaseToGoogleSheet();
        googleJobQuestDetailService.syncJobQuestDetailToGoogleSheet();
        return ResponseEntity.ok("✅ DB 데이터를 Google Sheets에 동기화했습니다.");
    }
}
