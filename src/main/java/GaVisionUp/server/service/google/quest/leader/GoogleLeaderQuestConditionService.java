package GaVisionUp.server.service.google.quest.leader;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.leader.LeaderQuestCondition;
import GaVisionUp.server.global.exception.RestApiException;
import GaVisionUp.server.global.exception.code.status.GlobalErrorStatus;
import GaVisionUp.server.repository.quest.leader.condition.LeaderQuestConditionRepository;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GoogleLeaderQuestConditionService {

    private final LeaderQuestConditionRepository leaderQuestConditionRepository;
    private final Sheets sheetsService;

    @Value("${google.sheets.spreadsheet-id}") // ✅ YML에서 스프레드시트 ID 가져오기
    private String spreadsheetId;

    private static final String RANGE_CONDITIONS = "참고. 리더부여 퀘스트!J11:R13"; // ✅ 리더 퀘스트 조건 범위
    private static final String RANGE_DEPARTMENT = "참고. 리더부여 퀘스트!J8"; // ✅ 소속 범위

    /**
     * ✅ Google Sheets에서 리더 퀘스트 조건 데이터를 읽어와 DB에 저장
     */
    public void syncLeaderQuestConditions() {
        try {
            // ✅ 소속 정보 가져오기
            ValueRange departmentResponse = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, RANGE_DEPARTMENT)
                    .execute();

            List<List<Object>> departmentValues = departmentResponse.getValues();
            if (departmentValues == null || departmentValues.isEmpty() || departmentValues.get(0).isEmpty()) {
                log.warn("⚠️ [WARN] Google Sheets에서 소속 정보를 찾을 수 없습니다.");
                throw new RestApiException(GlobalErrorStatus._INVALID_DEPARTMENT);
            }

            String departmentName = departmentValues.get(0).get(0).toString().trim();
            Department department = Department.fromString(departmentName);

            // ✅ 리더 퀘스트 조건 데이터 가져오기
            ValueRange conditionsResponse = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, RANGE_CONDITIONS)
                    .execute();

            List<List<Object>> conditionsValues = conditionsResponse.getValues();
            if (conditionsValues == null || conditionsValues.isEmpty()) {
                return;
            }

            for (List<Object> row : conditionsValues) {
                try {
                    // ✅ 각 컬럼 데이터 매핑
                    log.debug("📌 [DEBUG] 읽어온 행 데이터: {}", row);

                    String questName = row.get(0) != null ? row.get(0).toString().trim() : "";
                    Cycle cycle = row.get(1) != null ? Cycle.from(row.get(1).toString().trim()) : null;
                    String weightRaw = row.size() > 2 ? row.get(2).toString().trim() : "";
                    String totalExpRaw = row.size() > 3 ? row.get(3).toString().trim() : "";
                    String maxExpRaw = row.size() > 4 ? row.get(4).toString().trim() : "";
                    String medianExpRaw = row.size() > 5 ? row.get(5).toString().trim() : "";
                    String maxCondition = row.size() > 6 ? row.get(6).toString().trim() : "";
                    String medianCondition = row.size() > 7 ? row.get(7).toString().trim() : "";
                    String description = row.size() > 8 ? row.get(8).toString().trim() : "";

                    if (questName.isEmpty() || cycle == null || weightRaw.isEmpty() || totalExpRaw.isEmpty()
                            || maxExpRaw.isEmpty() || medianExpRaw.isEmpty() || maxCondition.isEmpty() || medianCondition.isEmpty()) {
                        continue;
                    }

                    double weight = Double.parseDouble(weightRaw.replace("%", "").trim());
                    int totalExp = Integer.parseInt(totalExpRaw.replace(",", "").trim());
                    int maxExp = Integer.parseInt(maxExpRaw.trim());
                    int medianExp = Integer.parseInt(medianExpRaw.trim());

                    // ✅ 기존 데이터 확인 및 저장/업데이트
                    Optional<LeaderQuestCondition> existingConditionOpt = leaderQuestConditionRepository
                            .findByDepartmentAndCycleAndQuestName(department, cycle, questName);

                    if (existingConditionOpt.isPresent()) {
                        LeaderQuestCondition existingCondition = existingConditionOpt.get();
                        existingCondition.updateCondition(weight, totalExp, maxExp, medianExp, maxCondition, medianCondition, description);
                        leaderQuestConditionRepository.save(existingCondition);
                    } else {
                        LeaderQuestCondition newCondition = LeaderQuestCondition.builder()
                                .department(department)
                                .cycle(cycle)
                                .questName(questName)
                                .weight(weight)
                                .totalExp(totalExp)
                                .maxExp(maxExp)
                                .medianExp(medianExp)
                                .maxCondition(maxCondition)
                                .medianCondition(medianCondition)
                                .description(description)
                                .build();
                        leaderQuestConditionRepository.save(newCondition);
                    }

                } catch (Exception e) {
                    log.error("❌ [ERROR] 리더 퀘스트 조건 처리 중 오류 발생: {}", row, e);
                }
            }
        } catch (IOException e) {
            log.error("❌ [ERROR] Google Sheets 데이터를 읽는 중 오류 발생", e);
        } catch (Exception e) {
            log.error("❌ [ERROR] 리더 퀘스트 조건 동기화 중 오류 발생", e);
        }
    }
}
