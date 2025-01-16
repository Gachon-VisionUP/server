/*
package GaVisionUp.server.web.controller.google;

import GaVisionUp.server.service.google.GoogleUserService;
import GaVisionUp.server.service.google.quest.GooglePerformanceService;
import GaVisionUp.server.service.google.quest.job.GoogleJobQuestDetailService;
import GaVisionUp.server.service.google.quest.job.GoogleJobQuestService;
import GaVisionUp.server.service.google.quest.leader.GoogleLeaderQuestConditionService;
import GaVisionUp.server.service.google.quest.leader.GoogleLeaderQuestService;
import GaVisionUp.server.web.dto.google.SheetUpdatePayload;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sync")
public class SyncController {

    private final GoogleUserService googleUserService;
    private final GooglePerformanceService googlePerformanceService;
    private final GoogleJobQuestDetailService googleJobQuestDetailService;
    private final GoogleJobQuestService googleJobQuestService;
    private final GoogleLeaderQuestConditionService googleLeaderQuestConditionService;
    private final GoogleLeaderQuestService googleLeaderQuestService;

    @PostMapping("/sheet-to-db")
    @Transactional
    public ResponseEntity<String> syncSheetToDb(@RequestBody SheetUpdatePayload payload) {
        String sheet = payload.getSheet();
        String row = payload.getRow();
        String column = payload.getColumn();
        String value = payload.getValue();

        log.info("Received payload: {}", payload);

        log.info("Received data:");
        log.info("Sheet Name: {}", sheet);
        log.info("Row: {}", row);
        log.info("Column: {}", column);
        log.info("Value: {}", value);

        log.info("R");

        */
/*googleUserService.syncUsersFromGoogleSheet();
        googlePerformanceService.syncH1PerformanceFromGoogleSheet();
        googlePerformanceService.syncH2PerformanceFromGoogleSheet();
        googleJobQuestDetailService.syncJobQuestDetailFromGoogleSheet();
        googleJobQuestService.syncJobQuestFromGoogleSheet();
        googleLeaderQuestConditionService.syncLeaderQuestConditions();
        googleLeaderQuestService.syncLeaderQuestsFromGoogleSheet();*//*


        // 특정 시트에 따라 동기화 로직 실행
        */
/*switch (sheetName) {
            case "Users":
                googleUserService.syncUsersFromGoogleSheet();
                break;
            case "H1Performance":
                googlePerformanceService.syncH1PerformanceFromGoogleSheet();
                break;
            case "H2Performance":
                googlePerformanceService.syncH2PerformanceFromGoogleSheet();
                break;
            case "JobQuests":
                googleJobQuestDetailService.syncJobQuestDetailFromGoogleSheet();
                break;
            case "LeaderQuests":
                googleLeaderQuestConditionService.syncLeaderQuestConditions();
                break;
            default:
                System.out.println("Unknown sheet: " + sheetName);
                break;
        }*//*

        return ResponseEntity.ok("✅ 동기화 완료: Sheet=" + sheet + ", Row=" + row + ", Column=" + column);
    }
}
*/
