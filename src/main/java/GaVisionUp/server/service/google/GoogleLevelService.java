package GaVisionUp.server.service.google;

import GaVisionUp.server.entity.Level;
import GaVisionUp.server.entity.enums.JobGroup;
import GaVisionUp.server.global.exception.RestApiException;
import GaVisionUp.server.repository.level.LevelRepository;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GoogleLevelService {

    private final LevelRepository levelRepository;
    private final Sheets sheetsService;

    @Value("${google.sheets.spreadsheet-id}") // ✅ YML에서 스프레드시트 ID 주입
    private String spreadsheetId;

    // ✅ 직군별 데이터 범위 설정
    private static final String RANGE_F = "참고. 레벨별 경험치!B9:C"; // F 직군
    private static final String RANGE_B = "참고. 레벨별 경험치!E9:F"; // B 직군
    private static final String RANGE_G = "참고. 레벨별 경험치!H9:I"; // G 직군
    private static final String RANGE_T = "참고. 레벨별 경험치!K9:L"; // T 직군

    /**
     * ✅ Google Sheets에서 레벨 데이터를 가져와 DB에 저장
     */
    public void syncLevelsFromGoogleSheet() {
        try {
            // ✅ 각 직군 데이터 가져오기 및 저장
            syncLevelDataForJobGroup("F", RANGE_F);
            syncLevelDataForJobGroup("B", RANGE_B);
            syncLevelDataForJobGroup("G", RANGE_G);
            syncLevelDataForJobGroup("T", RANGE_T);

        } catch (IOException e) {
            log.error("❌ [ERROR] Google Sheets 데이터를 읽는 중 오류 발생", e);
        } catch (Exception e) {
            log.error("❌ [ERROR] 레벨 데이터를 동기화하는 중 오류 발생", e);
        }
    }

    /**
     * ✅ 특정 직군의 레벨 데이터를 Google Sheets에서 읽어와 DB에 저장
     *
     * @param jobGroup 직군 이름 (예: "F", "B", "G", "T")
     * @param range    Google Sheets 범위 (예: "참고. 레벨!B9:C")
     */
    private void syncLevelDataForJobGroup(String jobGroup, String range) throws IOException {
        // ✅ Google Sheets 데이터 읽기
        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            log.warn("⚠️ [WARN] Google Sheets에서 직군 '{}'의 레벨 데이터를 찾을 수 없습니다.", jobGroup);
            return;
        }

        for (List<Object> row : values) {
            if (row.size() < 1) {
                continue;
            }

            try {
                // ✅ 데이터 매핑
                String levelName = row.get(0).toString().trim();

                // ✅ 경험치 값 처리
                int requiredExp = 0; // 기본값 0
                if (row.size() > 1 && row.get(1) != null && !row.get(1).toString().trim().isEmpty()) {
                    try {
                        // 쉼표 제거 후 정수로 변환
                        requiredExp = Integer.parseInt(row.get(1).toString().trim().replace(",", ""));
                    } catch (NumberFormatException e) {
                        log.warn("⚠️ [WARN] 총 필요 경험치 변환 실패 - 값: {}, 기본값 0으로 처리", row.get(1));
                    }
                }
                // ✅ 레벨 데이터 저장 또는 업데이트
                final Integer finalRequiredExp = requiredExp; // 람다식 내부에서 사용될 final 변수 생성

                Level level = levelRepository.findByJobGroupAndLevelName(JobGroup.from(jobGroup), levelName)
                        .map(existing -> {
                            existing.updateRequiredExp(finalRequiredExp); // 기존 데이터 업데이트
                            return existing;
                        })
                        .orElseGet(() -> Level.create(JobGroup.from(jobGroup), levelName, finalRequiredExp)); // 새 데이터 생성

                levelRepository.save(level);

            } catch (Exception e) {
                log.error("❌ [ERROR] 레벨 데이터 처리 중 오류 발생: {}", row, e);
            }
        }
    }
}
