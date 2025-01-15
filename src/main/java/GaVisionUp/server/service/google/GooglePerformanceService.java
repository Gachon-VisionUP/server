package GaVisionUp.server.service.google;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.enums.PerformanceGrade;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.entity.PerformanceReview;
import GaVisionUp.server.repository.exp.experience.ExperienceRepository;
import GaVisionUp.server.repository.performance.PerformanceReviewRepository;
import GaVisionUp.server.repository.user.UserRepository;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GooglePerformanceService {

    private final UserRepository userRepository;
    private final PerformanceReviewRepository performanceReviewRepository;
    private final ExperienceRepository experienceRepository;
    private final Sheets sheetsService;

    private static final String SPREADSHEET_ID = ""; // ✅ Google 스프레드시트 ID
    private static final String RANGE_H1PERFORMANCE = "참고. 인사평가!B10:F34"; // ✅ 인사평가 입력 범위
    private static final String RANGE_H2PERFORMANCE = "참고. 인사평가!H10:L34"; // ✅ 인사평가 입력 범위
    private static final Pattern EMPLOYEE_ID_PATTERN = Pattern.compile("^\\d{10}$"); // ✅ 사번 형식 체크

    /**
     * ✅ Google Sheets → DB 동기화 (상반기 인사평가 저장)
     */
    public void syncH1PerformanceFromGoogleSheet() {
        try {
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(SPREADSHEET_ID, RANGE_H1PERFORMANCE)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                log.warn("⚠️ [WARN] Google Sheets에서 인사평가 데이터를 찾을 수 없습니다.");
                return;
            }

            for (List<Object> row : values) {
                if (row.isEmpty() || row.size() < 4) {
                    log.warn("⚠️ [WARN] 데이터 부족으로 인해 인사평가 정보를 저장할 수 없습니다. {}", row);
                    continue;
                }

                try {
                    // ✅ 사번 검증
                    String employeeId = row.get(0).toString().trim();
                    if (!EMPLOYEE_ID_PATTERN.matcher(employeeId).matches()) {
                        log.warn("⚠️ [WARN] 잘못된 사번 형식으로 인해 인사평가 정보를 저장할 수 없습니다: {}", employeeId);
                        continue;
                    }

                    Optional<User> optionalUser = userRepository.findByEmployeeId(employeeId);
                    if (optionalUser.isEmpty()) {
                        log.warn("⚠️ [WARN] 사번 '{}'에 해당하는 사용자를 찾을 수 없습니다.", employeeId);
                        continue;
                    }
                    User user = optionalUser.get();

                    String grade = row.get(2).toString().trim(); // ✅ 인사평가 등급 (S, A, B, C, D)
                    PerformanceGrade performanceGrade = PerformanceGrade.fromString(grade); // ✅ 문자열 → Enum 변환
                    int grantedExp = performanceGrade.getExp(); // ✅ Enum에서 경험치 값 가져오기

                    // ✅ PerformanceReview 생성
                    PerformanceReview review = PerformanceReview.create(user, ExpType.H1_PERFORMANCE, performanceGrade);
                    performanceReviewRepository.save(review);

                    // ✅ 경험치 저장
                    Experience experience = new Experience(user, ExpType.H1_PERFORMANCE, grantedExp);
                    experienceRepository.save(experience);

                    log.info("✅ [INFO] 인사평가 '{}' 저장 완료 (사번: {}, 경험치: {})", grade, employeeId, grantedExp);

                } catch (Exception e) {
                    log.error("❌ [ERROR] 인사평가 데이터 변환 중 오류 발생: {}", row, e);
                }
            }

            log.info("✅ [INFO] Google Sheets에서 인사평가 데이터를 성공적으로 동기화했습니다.");

        } catch (IOException e) {
            log.error("❌ [ERROR] Google Sheets에서 인사평가 데이터를 동기화하는 중 오류 발생", e);
        }
    }

    /**
     * ✅ Google Sheets → DB 동기화 (하반기 인사평가 저장)
     */
    public void syncH2PerformanceFromGoogleSheet() {
        try {
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(SPREADSHEET_ID, RANGE_H2PERFORMANCE)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                log.warn("⚠️ [WARN] Google Sheets에서 인사평가 데이터를 찾을 수 없습니다.");
                return;
            }

            for (List<Object> row : values) {
                if (row.isEmpty() || row.size() < 4) {
                    log.warn("⚠️ [WARN] 데이터 부족으로 인해 인사평가 정보를 저장할 수 없습니다. {}", row);
                    continue;
                }

                try {
                    // ✅ 사번 검증
                    String employeeId = row.get(0).toString().trim();
                    if (!EMPLOYEE_ID_PATTERN.matcher(employeeId).matches()) {
                        log.warn("⚠️ [WARN] 잘못된 사번 형식으로 인해 인사평가 정보를 저장할 수 없습니다: {}", employeeId);
                        continue;
                    }

                    Optional<User> optionalUser = userRepository.findByEmployeeId(employeeId);
                    if (optionalUser.isEmpty()) {
                        log.warn("⚠️ [WARN] 사번 '{}'에 해당하는 사용자를 찾을 수 없습니다.", employeeId);
                        continue;
                    }
                    User user = optionalUser.get();

                    String grade = row.get(2).toString().trim(); // ✅ 인사평가 등급 (S, A, B, C, D)
                    PerformanceGrade performanceGrade = PerformanceGrade.fromString(grade); // ✅ 문자열 → Enum 변환
                    int grantedExp = performanceGrade.getExp(); // ✅ Enum에서 경험치 값 가져오기

                    // ✅ PerformanceReview 생성
                    PerformanceReview review = PerformanceReview.create(user, ExpType.H2_PERFORMANCE, performanceGrade);
                    performanceReviewRepository.save(review);

                    // ✅ 경험치 저장
                    Experience experience = new Experience(user, ExpType.H2_PERFORMANCE, grantedExp);
                    experienceRepository.save(experience);

                    log.info("✅ [INFO] 인사평가 '{}' 저장 완료 (사번: {}, 경험치: {})", grade, employeeId, grantedExp);

                } catch (Exception e) {
                    log.error("❌ [ERROR] 인사평가 데이터 변환 중 오류 발생: {}", row, e);
                }
            }

            log.info("✅ [INFO] Google Sheets에서 인사평가 데이터를 성공적으로 동기화했습니다.");

        } catch (IOException e) {
            log.error("❌ [ERROR] Google Sheets에서 인사평가 데이터를 동기화하는 중 오류 발생", e);
        }
    }
}