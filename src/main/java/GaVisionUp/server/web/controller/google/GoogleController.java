package GaVisionUp.server.web.controller.google;

import GaVisionUp.server.service.google.GoogleLevelService;
import GaVisionUp.server.service.google.quest.GoogleEntireProjectService;
import GaVisionUp.server.service.google.GooglePostService;
import GaVisionUp.server.service.google.quest.job.GoogleJobQuestDetailService;
import GaVisionUp.server.service.google.quest.GooglePerformanceService;
import GaVisionUp.server.service.google.GoogleUserService;
import GaVisionUp.server.service.google.quest.job.GoogleJobQuestService;
import GaVisionUp.server.service.google.quest.leader.GoogleLeaderQuestConditionService;

import GaVisionUp.server.service.google.quest.leader.GoogleLeaderQuestService;
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
    private final GoogleLeaderQuestService googleLeaderQuestService;
    private final GoogleEntireProjectService googleEntireProjectService;
    private final GooglePostService googlePostService;
    private final GoogleLevelService googleLevelService;

    /**
     * ✅ Google Sheets에서 유저 데이터 동기화 API
     */
    @PostMapping("/sync-sheet-to-db/user")
    public ResponseEntity<String> userSyncSheetToDb() {
        googleUserService.syncUsersFromGoogleSheet();
        googleEntireProjectService.syncEntireProjects();
        googleLevelService.syncLevelsFromGoogleSheet();
        return ResponseEntity.ok("✅ Google Sheets에서 DB에 데이터를 동기화했습니다.");
    }

    @PostMapping("/sync-sheet-to-db/performance")
    public ResponseEntity<String> performanceSyncSheetToDb() {
        googlePerformanceService.syncH1PerformanceFromGoogleSheet();
        googlePerformanceService.syncH2PerformanceFromGoogleSheet();
        return ResponseEntity.ok("✅ Google Sheets에서 DB에 데이터를 동기화했습니다.");
    }

    @PostMapping("/sync-sheet-to-db/job-quest")
    public ResponseEntity<String> jobQuestSyncSheetToDb() {
        googleJobQuestDetailService.syncJobQuestDetailFromGoogleSheet();
        googleJobQuestService.syncJobQuestFromGoogleSheet();
        return ResponseEntity.ok("✅ Google Sheets에서 DB에 데이터를 동기화했습니다.");
    }

    @PostMapping("/sync-sheet-to-db/leader-quest")
    public ResponseEntity<String> leaderQuestSyncSheetToDb() {
        googleLeaderQuestConditionService.syncLeaderQuestConditions();
        googleLeaderQuestService.syncLeaderQuestsFromGoogleSheet();
        return ResponseEntity.ok("✅ Google Sheets에서 DB에 데이터를 동기화했습니다.");
    }

    /**
     * ✅ DB → Google Sheets 동기화 (DB → Google Sheets)
     */
    @PostMapping("/sync-db-to-sheets")
    public ResponseEntity<String> syncDbToGoogleSheets() {
        googleUserService.syncDatabaseToGoogleSheet();
        googlePostService.syncBoardToGoogleSheet();
        return ResponseEntity.ok("✅ DB 데이터를 Google Sheets에 동기화했습니다.");
    }
}
