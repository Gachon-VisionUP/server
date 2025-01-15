package GaVisionUp.server.service.google;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.job.JobQuestDetail;

import GaVisionUp.server.global.exception.RestApiException;
import GaVisionUp.server.global.exception.code.status.GlobalErrorStatus;
import GaVisionUp.server.repository.quest.job.detail.JobQuestDetailRepository;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GoogleJobQuestService {

    private final JobQuestDetailRepository jobQuestDetailRepository;
    private final Sheets sheetsService;

    @Value("${google.sheets.spreadsheet-id}") // ✅ YML에서 스프레드시트 ID 가져오기
    private String spreadsheetId;

    private static final String RANGE_JOB_QUEST = "참고. 직무별 퀘스트!J14:S378"; // ✅ 직무 퀘스트 입력 범위
    private static final String RANGE_DEPARTMENT_INFO = "참고. 직무별 퀘스트!F11:H11"; // ✅ 소속, 직무그룹, 주기

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yy-M-d"); // ✅ '25-1-1' 같은 형식 대응

    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("❌ [ERROR] 날짜 변환 실패: '{}' (올바른 형식: yy-M-d)", dateStr, e);
            throw new IllegalArgumentException("올바른 날짜 형식이 아닙니다: " + dateStr);
        }
    }
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
     * ✅ Google Sheets → DB 동기화 (직무 퀘스트 저장)
     */
    public void syncJobQuestFromGoogleSheet() {
        try {
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, RANGE_JOB_QUEST)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                log.warn("⚠️ [WARN] Google Sheets에서 직무 퀘스트 데이터를 찾을 수 없습니다.");
                return;
            }

            // ✅ 부서, 직무그룹, 주기 정보 가져오기
            Department department = getDepartmentInfo();
            int part = getPartInfo();
            Cycle cycle = getCycleInfo();

            for (List<Object> row : values) {
                if (row.isEmpty() || row.size() < 9) {
                    continue;
                }

                try {
                    // ✅ 날짜 파싱
                    LocalDate recordedDate = parseDate(row.get(2).toString().trim()); // ✅ 날짜 변환 적용


                    // ✅ 데이터 파싱
                    int month = Integer.parseInt(row.get(0).toString().trim());
                    int round = Integer.parseInt(row.get(1).toString().trim());
                    double sales = Double.parseDouble(row.get(4).toString().trim());
                    double designCost = Double.parseDouble(row.get(6).toString().trim());
                    double employeeSalary = Double.parseDouble(row.get(7).toString().trim());
                    double retirementSalary = Double.parseDouble(row.get(8).toString().trim());
                    double insuranceFee = Double.parseDouble(row.get(9).toString().trim());

                    // ✅ 기존 데이터 확인 및 업데이트
                    Optional<JobQuestDetail> existingQuestOpt = jobQuestDetailRepository.findByRecordedDate(recordedDate);
                    if (existingQuestOpt.isPresent()) {
                        JobQuestDetail existingQuest = existingQuestOpt.get();
                        existingQuest.updateJobQuest(sales, designCost, employeeSalary, retirementSalary, insuranceFee);
                        jobQuestDetailRepository.save(existingQuest);
                        log.info("🔄 [UPDATE] 기존 직무 퀘스트 업데이트 완료 (날짜: {})", recordedDate);
                    } else {
                        JobQuestDetail jobQuest = JobQuestDetail.create(
                                department, part, cycle, month, round, sales, designCost, employeeSalary, retirementSalary, insuranceFee, recordedDate
                        );
                        jobQuestDetailRepository.save(jobQuest);
                        log.info("✅ [INSERT] 새로운 직무 퀘스트 저장 완료 (날짜: {})", recordedDate);
                    }

                } catch (Exception e) {
                    log.error("❌ [ERROR] 직무 퀘스트 데이터 변환 중 오류 발생: {}", row, e);
                }
            }

        } catch (IOException e) {
            log.error("❌ [ERROR] Google Sheets에서 직무 퀘스트 데이터를 동기화하는 중 오류 발생", e);
        }
    }

    /**
     * ✅ 날짜 변환 (DB → Google Sheets 저장 시)
     */
    private String formatDate(LocalDate date) {
        return DATE_FORMATTER.format(date);
    }

    /**
     * ✅ DB → Google Sheets 동기화 (직무 퀘스트 저장)
     */
    public void syncJobQuestToGoogleSheet() {
        try {
            List<JobQuestDetail> jobQuestList = jobQuestDetailRepository.findAllJobQuests();
            if (jobQuestList.isEmpty()) {
                log.warn("⚠️ [WARN] DB에서 직무 퀘스트 데이터를 찾을 수 없습니다.");
                return;
            }

            // ✅ DB 데이터 → Google Sheets에 입력할 리스트로 변환
            List<List<? extends Serializable>> sheetData = jobQuestList.stream().map(quest -> Arrays.asList(
                    quest.getMonth(),
                    quest.getRound(),
                    formatDate(quest.getRecordedDate()), // ✅ 날짜 형식 변환 (yy-M-d)
                    "", // 요일은 수동 입력
                    quest.getSales(),
                    quest.getLaborCost(),
                    quest.getDesignCost(),
                    quest.getEmployeeSalary(),
                    quest.getRetirementSalary(),
                    quest.getInsuranceFee()
            )).collect(Collectors.toList());

            // ✅ Google Sheets에 데이터 업데이트
            sheetsService.spreadsheets().values()
                    .update(spreadsheetId, RANGE_JOB_QUEST, new ValueRange().setValues((List<List<Object>>) (List<?>) sheetData))
                    .setValueInputOption("RAW")
                    .execute();

            log.info("✅ [INFO] 직무별 상세 데이터를 Google Sheets에 성공적으로 동기화했습니다.");

        } catch (IOException e) {
            log.error("❌ [ERROR] DB 데이터를 Google Sheets에 동기화하는 중 오류 발생", e);
        }
    }
}
