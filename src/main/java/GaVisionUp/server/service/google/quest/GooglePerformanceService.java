package GaVisionUp.server.service.google.quest;

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
import org.springframework.beans.factory.annotation.Value;

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

    @Value("${google.sheets.spreadsheet-id}") // ✅ YML에서 스프레드시트 ID 주입
    private String spreadsheetId;
    private static final String RANGE_H1PERFORMANCE = "참고. 인사평가!B10:F34"; // ✅ 상반기 인사평가 입력 범위
    private static final String RANGE_H2PERFORMANCE = "참고. 인사평가!H10:L34"; // ✅ 하반기 인사평가 입력 범위
    private static final Pattern EMPLOYEE_ID_PATTERN = Pattern.compile("^\\d{10}$"); // ✅ 사번 형식 체크

    /**
     * ✅ Google Sheets → DB 동기화 (상반기 인사평가 저장)
     */
    public void syncH1PerformanceFromGoogleSheet() {
        syncPerformanceFromGoogleSheet(RANGE_H1PERFORMANCE, ExpType.H1_PERFORMANCE);
    }

    /**
     * ✅ Google Sheets → DB 동기화 (하반기 인사평가 저장)
     */
    public void syncH2PerformanceFromGoogleSheet() {
        syncPerformanceFromGoogleSheet(RANGE_H2PERFORMANCE, ExpType.H2_PERFORMANCE);
    }

    /**
     * ✅ Google Sheets → DB 동기화 (공통 로직)
     */
    private void syncPerformanceFromGoogleSheet(String range, ExpType expType) {
        try {
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                log.warn("⚠️ [WARN] Google Sheets에서 인사평가 데이터를 찾을 수 없습니다.");
                return;
            }

            for (List<Object> row : values) {
                if (row.isEmpty() || row.size() < 4) {
                    continue;
                }

                try {
                    // ✅ 사번 검증
                    String employeeId = row.get(0).toString().trim();
                    if (!EMPLOYEE_ID_PATTERN.matcher(employeeId).matches()) {
                        continue;
                    }

                    Optional<User> optionalUser = userRepository.findByEmployeeId(employeeId);
                    if (optionalUser.isEmpty()) {
                        continue;
                    }
                    User user = optionalUser.get();

                    String gradeStr = row.get(2).toString().trim(); // ✅ 인사평가 등급 (S, A, B, C, D)
                    PerformanceGrade performanceGrade = PerformanceGrade.fromString(gradeStr); // ✅ 문자열 → Enum 변환
                    int newExp = performanceGrade.getExp(); // ✅ Enum에서 경험치 값 가져오기
                    int year = java.time.Year.now().getValue(); // 현재 연도 가져오기

                    // ✅ 기존 인사평가 조회
                    Optional<PerformanceReview> existingReviewOpt = performanceReviewRepository.findByUserIdAndYearAndExpType(user.getId(), year, expType);

                    if (existingReviewOpt.isPresent()) {
                        PerformanceReview existingReview = existingReviewOpt.get();
                        int previousExp = existingReview.getGrantedExp();

                        // ✅ 기존 경험치와 동일하면 중복 저장 방지
                        if (previousExp == newExp) {
                            continue;
                        }

                        // ✅ 경험치 차이 계산 (양수: 추가, 음수: 차감)
                        int expDiff = newExp - previousExp;

                        // ✅ 기존 경험치 ID 조회 후 업데이트
                        Optional<Long> expIdOpt = experienceRepository.findExperienceIdByUserAndYear(user.getId(), expType, year);
                        if (expIdOpt.isPresent()) {
                            experienceRepository.updateExperienceById(expIdOpt.get(), newExp);
                        } else {
                            // ✅ 기존 경험치가 없으면 새로 저장
                            Experience newExperience = new Experience(user, expType, newExp);
                            experienceRepository.save(newExperience);
                        }

                        // ✅ 유저의 총 경험치 반영
                        user.addExperience(expDiff);
                        userRepository.save(user); // ✅ 경험치 업데이트 후 저장

                        // ✅ 기존 리뷰 업데이트
                        existingReview.updateReview(performanceGrade, newExp, expType);
                        performanceReviewRepository.save(existingReview);

                    } else {
                        // ✅ PerformanceReview 생성
                        PerformanceReview review = PerformanceReview.create(user, expType, performanceGrade);
                        performanceReviewRepository.save(review);

                        // ✅ 경험치 저장
                        Experience experience = new Experience(user, expType, newExp);
                        experienceRepository.save(experience);

                        // ✅ 유저의 총 경험치 반영
                        userRepository.save(user);

                    }

                } catch (Exception e) {
                    log.error("❌ [ERROR] 인사평가 데이터 변환 중 오류 발생: {}", row, e);
                }
            }

        } catch (IOException e) {
            log.error("❌ [ERROR] Google Sheets에서 인사평가 데이터를 동기화하는 중 오류 발생", e);
        }
    }
}
