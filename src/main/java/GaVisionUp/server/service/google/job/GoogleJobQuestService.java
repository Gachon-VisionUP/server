package GaVisionUp.server.service.google.job;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;

import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.enums.TeamQuestGrade;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.entity.quest.job.JobQuest;

import GaVisionUp.server.global.exception.RestApiException;
import GaVisionUp.server.global.exception.code.status.GlobalErrorStatus;
import GaVisionUp.server.repository.exp.experience.ExperienceRepository;
import GaVisionUp.server.repository.quest.job.JobQuestRepository;
import GaVisionUp.server.repository.user.UserRepository;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional // 클래스 레벨 트랜잭션 관리
public class GoogleJobQuestService {

    private final JobQuestRepository jobQuestRepository;
    private final UserRepository userRepository;
    private final ExperienceRepository experienceRepository;
    private final Sheets sheetsService;

    @Value("${google.sheets.spreadsheet-id}") // ✅ YML에서 스프레드시트 ID 가져오기
    private String spreadsheetId;

    private static final String RANGE_DETAILS = "참고. 직무별 퀘스트!F14:I65"; // 직무별 퀘스트 상세
    private static final String RANGE_META = "참고. 직무별 퀘스트!B11:C11"; // MAX 점수, MED 점수
    private static final String RANGE_DEPARTMENT_INFO = "참고. 직무별 퀘스트!F11:H11"; // ✅ 소속, 직무그룹, 주기


    /**
     * ✅ Google Sheets에서 부서 정보 가져오기
     */
    public Department getDepartmentInfo() {
        try {
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, RANGE_DEPARTMENT_INFO)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty() || values.get(0).isEmpty()) {
                log.warn("⚠️ [WARN] Google Sheets에서 부서 정보를 찾을 수 없습니다.");
                throw new RestApiException(GlobalErrorStatus._INVALID_DEPARTMENT);
            }

            String departmentName = values.get(0).get(0).toString().trim();
            return Department.fromString(departmentName);

        } catch (IOException e) {
            log.error("❌ [ERROR] Google Sheets에서 부서 정보를 가져오는 중 오류 발생", e);
            throw new RestApiException(GlobalErrorStatus._INVALID_DEPARTMENT);
        }
    }

    /**
     * ✅ Google Sheets에서 직무 그룹(파트) 정보 가져오기
     */
    public int getPartInfo() {
        try {
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, RANGE_DEPARTMENT_INFO)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty() || values.get(0).size() < 2) {
                log.warn("⚠️ [WARN] Google Sheets에서 직무 그룹 정보를 찾을 수 없습니다.");
                throw new RestApiException(GlobalErrorStatus._INVALID_PART);
            }

            String partValue = values.get(0).get(1).toString().trim();
            return Integer.parseInt(partValue);

        } catch (IOException | NumberFormatException e) {
            log.error("❌ [ERROR] Google Sheets에서 직무 그룹 정보를 가져오는 중 오류 발생", e);
            throw new RestApiException(GlobalErrorStatus._INVALID_PART);
        }
    }

    /**
     * ✅ Google Sheets에서 주기 정보 가져오기
     */
    public Cycle getCycleInfo() {
        try {
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, RANGE_DEPARTMENT_INFO)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty() || values.get(0).size() < 3) {
                log.warn("⚠️ [WARN] Google Sheets에서 주기 정보를 찾을 수 없습니다.");
                throw new RestApiException(GlobalErrorStatus._INVALID_CYCLE);
            }

            String cycleValue = values.get(0).get(2).toString().trim();
            return Cycle.from(cycleValue);

        } catch (IOException e) {
            log.error("❌ [ERROR] Google Sheets에서 주기 정보를 가져오는 중 오류 발생", e);
            throw new RestApiException(GlobalErrorStatus._INVALID_CYCLE);
        }
    }

    /**
     * ✅ Google Sheets 데이터를 활용한 직무별 퀘스트 평가
     */
    public void syncJobQuestFromGoogleSheet() {
        try {
            // ✅ 메타데이터 읽기 (MAX 점수, MED 점수)
            ValueRange metaResponse = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, RANGE_META)
                    .execute();
            List<List<Object>> metaValues = metaResponse.getValues();

            if (metaValues == null || metaValues.isEmpty() || metaValues.get(0).size() < 2) {
                throw new IllegalArgumentException("MAX 점수와 MED 점수를 읽어올 수 없습니다.");
            }

            // ✅ 부서, 직무그룹, 주기 정보 가져오기
            Department department = getDepartmentInfo();
            int part = getPartInfo();
            Cycle cycle = getCycleInfo();

            // ✅ MAX 점수, MED 점수
            int maxExp = Integer.parseInt(metaValues.get(0).get(0).toString().trim()); // B11
            int medExp = Integer.parseInt(metaValues.get(0).get(1).toString().trim()); // C11

            // ✅ 상세 데이터 읽기
            ValueRange detailsResponse = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, RANGE_DETAILS)
                    .execute();
            List<List<Object>> detailValues = detailsResponse.getValues();

            if (detailValues == null || detailValues.isEmpty()) {
                throw new IllegalArgumentException("직무별 퀘스트 상세 데이터를 읽어올 수 없습니다.");
            }

            for (List<Object> row : detailValues) {
                if (row.size() < 4 || row.get(3) == null || row.get(3).toString().trim().isEmpty()) {
                    continue; // ✅ 유효하지 않은 데이터는 건너뛰기
                }

                try {
                    int round = Integer.parseInt(row.get(2).toString().trim()); // ✅ H열의 주차 값
                    double productivity = Double.parseDouble(row.get(3).toString().trim());

                    if (productivity == 0.0) {
                        continue;
                    }

                    // ✅ 각 주차별 F13 + round, G13 + round의 값을 읽어오기
                    String maxConditionRange = "참고. 직무별 퀘스트!F" + (13 + round); // F13 + 주차
                    String medConditionRange = "참고. 직무별 퀘스트!G" + (13 + round); // G13 + 주차

                    ValueRange maxConditionResponse = sheetsService.spreadsheets().values()
                            .get(spreadsheetId, maxConditionRange)
                            .execute();
                    ValueRange medConditionResponse = sheetsService.spreadsheets().values()
                            .get(spreadsheetId, medConditionRange)
                            .execute();

                    double maxCondition = Double.parseDouble(maxConditionResponse.getValues().get(0).get(0).toString().trim());
                    double medCondition = Double.parseDouble(medConditionResponse.getValues().get(0).get(0).toString().trim());

                    String note = (row.size() > 4) ? row.get(4).toString().trim() : ""; // ✅ D열 비고 값

                    // ✅ 평가 기준 및 경험치 설정
                    int grantedExp; // 새로 부여될 경험치
                    int previousGrantedExp = 0; // 기존 부여된 경험치
                    TeamQuestGrade grade;

                    if (productivity >= maxCondition) {
                        grade = TeamQuestGrade.MAX;
                        grantedExp = maxExp;
                    } else if (productivity >= medCondition) {
                        grade = TeamQuestGrade.MEDIAN;
                        grantedExp = medExp;
                    } else {
                        grade = TeamQuestGrade.MIN;
                        grantedExp = 0;
                    }

                    log.info("📌 [DEBUG] 주기: {}, 주차: {}, 생산성: {}, 평가 등급: {}, 부여 경험치: {}, maxCondition: {}, medCondition: {}",
                            cycle, round, productivity, grade, grantedExp, maxCondition, medCondition);

                    // ✅ JobQuest 저장 또는 업데이트
                    Optional<JobQuest> existingQuestOpt = jobQuestRepository.findByDepartmentAndPartAndCycleAndRound(
                            department, part, cycle, round);

                    if (existingQuestOpt.isPresent()) {
                        log.warn("⚠️ [WARN] 중복된 JobQuest가 존재합니다. 기존 데이터를 업데이트합니다.");
                        JobQuest existingQuest = existingQuestOpt.get();
                        previousGrantedExp = existingQuest.getGrantedExp(); // 기존 부여된 경험치 가져오기
                        existingQuest.updateJobQuest(productivity, maxCondition, medCondition, grade, grantedExp, note);
                        jobQuestRepository.save(existingQuest);
                    } else {
                        JobQuest jobQuest = JobQuest.create(
                                department, part, cycle, round, productivity,
                                maxCondition, medCondition, maxExp, medExp, grade, grantedExp, note);
                        jobQuestRepository.save(jobQuest);
                    }

                    // ✅ 유저 경험치 부여
                    List<User> users = userRepository.findByDepartmentAndPart(department, part);
                    for (User user : users) {
                        // ✅ 기존 경험치를 마이너스로 저장
                        if (previousGrantedExp != 0) {
                            user.minusExperience(previousGrantedExp); // 기존 경험치 제거
                        }

                        // ✅ 새로운 경험치를 저장
                        if (grantedExp != 0) {
                            Experience newExperience = new Experience(user, ExpType.JOB_QUEST, grantedExp - previousGrantedExp);
                            experienceRepository.edit(newExperience);
                        }

                        // ✅ 유저 총 경험치 업데이트
                        userRepository.save(user);
                    }

                } catch (Exception e) {
                    log.error("❌ [ERROR] 주차 데이터를 처리하는 중 오류 발생: {}", row, e);
                }
            }

            log.info("✅ [INFO] Google Sheets 데이터를 기반으로 JobQuest 평가 및 경험치 부여 완료");

        } catch (IOException e) {
            log.error("❌ [ERROR] Google Sheets 데이터를 읽는 중 오류 발생", e);
        } catch (Exception e) {
            log.error("❌ [ERROR] 직무별 퀘스트 평가 중 오류 발생", e);
        }
    }
}
