package GaVisionUp.server.service.google.quest.leader;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.leader.LeaderQuest;
import GaVisionUp.server.entity.quest.leader.LeaderQuestCondition;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.global.exception.RestApiException;
import GaVisionUp.server.global.exception.code.status.GlobalErrorStatus;
import GaVisionUp.server.repository.exp.experience.ExperienceRepository;
import GaVisionUp.server.repository.quest.leader.LeaderQuestRepository;
import GaVisionUp.server.repository.quest.leader.condition.LeaderQuestConditionRepository;
import GaVisionUp.server.repository.user.UserRepository;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GoogleLeaderQuestService {

    private final LeaderQuestRepository leaderQuestRepository;
    private final LeaderQuestConditionRepository leaderQuestConditionRepository;
    private final UserRepository userRepository;
    private final ExperienceRepository experienceRepository;
    private final Sheets sheetsService;

    @Value("${google.sheets.spreadsheet-id}") // ✅ YML에서 스프레드시트 ID 가져오기
    private String spreadsheetId;

    private static final String RANGE_DETAILS = "참고. 리더부여 퀘스트!B10:I50"; // ✅ 데이터 입력 범위

    /**
     * ✅ Google Sheets 데이터를 활용한 리더 부여 퀘스트 처리
     */
    public void syncLeaderQuestsFromGoogleSheet() {
        try {
            // ✅ 리더 부여 퀘스트 데이터 가져오기
            ValueRange detailsResponse = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, RANGE_DETAILS)
                    .execute();

            List<List<Object>> detailValues = detailsResponse.getValues();

            if (detailValues == null || detailValues.isEmpty()) {
                log.warn("⚠️ [WARN] Google Sheets에서 리더 부여 퀘스트 데이터를 찾을 수 없습니다.");
                return;
            }

            for (List<Object> row : detailValues) {
                // ✅ 필수 값 체크 (월, 사번, 리더 부여 퀘스트명, 달성 내용, 부여 경험치)
                if (row.size() < 6 || row.get(0) == null || row.get(1) == null ||
                        row.get(3) == null || row.get(4) == null || row.get(5) == null) {
                    log.warn("⚠️ [WARN] 데이터가 부족하여 퀘스트를 처리할 수 없습니다. {}", row);
                    continue;
                }

                try {
                    // ✅ 데이터 매핑
                    int month = Integer.parseInt(row.get(0).toString().trim());
                    Integer week = (row.size() > 6 && row.get(6) != null && !row.get(6).toString().trim().isEmpty())
                            ? Integer.parseInt(row.get(6).toString().trim()) : null; // ✅ 주차는 선택 사항
                    String userIdStr = row.get(1).toString().trim();
                    String questName = row.get(3).toString().trim();
                    String achievementType = row.get(4).toString().trim();
                    int newGrantedExp = Integer.parseInt(row.get(5).toString().trim());
                    String note = (row.size() > 7 && row.get(7) != null) ? row.get(7).toString().trim() : "";

                    // ✅ 유저 조회
                    Optional<User> userOpt = userRepository.findByEmployeeId(userIdStr);
                    if (userOpt.isEmpty()) {
                        continue;
                    }
                    User user = userOpt.get();

                    // ✅ 리더 부여 퀘스트 조건 조회
                    Optional<LeaderQuestCondition> conditionOpt = leaderQuestConditionRepository.findByDepartmentAndCycleAndQuestName(
                            user.getDepartment(), Cycle.MONTHLY, questName);

                    if (conditionOpt.isEmpty()) {
                        continue;
                    }
                    LeaderQuestCondition condition = conditionOpt.get();

                    // ✅ 기존 퀘스트 기록 확인
                    Optional<LeaderQuest> existingQuestOpt = leaderQuestRepository.findByUserAndQuestNameAndMonthAndWeek(
                            user, questName, month, week);

                    int previousGrantedExp = 0; // 기존 경험치 초기화
                    if (existingQuestOpt.isPresent()) {
                        LeaderQuest existingQuest = existingQuestOpt.get();
                        previousGrantedExp = existingQuest.getGrantedExp(); // 기존 부여 경험치
                        // ✅ 유저 경험치 부여
                        int experienceDifference = newGrantedExp - previousGrantedExp;
                        if (experienceDifference != 0) {
                            Experience newExperience = new Experience(user, ExpType.LEADER_QUEST, experienceDifference);
                            experienceRepository.edit(newExperience);
                        }

                        // ✅ 퀘스트 업데이트
                        existingQuest.updateQuest(achievementType, newGrantedExp, note, LocalDate.now());
                        leaderQuestRepository.save(existingQuest);
                    } else {
                        LeaderQuest leaderQuest = LeaderQuest.create(
                                user, Cycle.MONTHLY, questName, month, week,
                                achievementType, newGrantedExp, note,
                                LocalDate.now(), condition);
                        Experience experience = new Experience(user, ExpType.LEADER_QUEST, newGrantedExp);
                        experienceRepository.save(experience);
                        leaderQuestRepository.save(leaderQuest);
                    }

                    // ✅ 유저 총 경험치 업데이트
                    userRepository.save(user);

                } catch (Exception e) {
                    log.error("❌ [ERROR] 리더 부여 퀘스트 처리 중 오류 발생: {}", row, e);
                }
            }
        } catch (IOException e) {
            log.error("❌ [ERROR] Google Sheets 데이터를 읽는 중 오류 발생", e);
        } catch (Exception e) {
            log.error("❌ [ERROR] 리더 부여 퀘스트 처리 중 오류 발생", e);
        }
    }
}
