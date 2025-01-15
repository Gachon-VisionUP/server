package GaVisionUp.server.service.google.user;

import GaVisionUp.server.entity.Level;
import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.Role;
import GaVisionUp.server.repository.level.LevelRepository;
import GaVisionUp.server.repository.user.UserRepository;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleUserService {

    private final UserRepository userRepository;
    private final LevelRepository levelRepository;
    private final Sheets sheetsService;

    private static final String SPREADSHEET_ID = ""; // ✅ Google 스프레드시트 ID
    private static final String RANGE = "참고. 구성원 정보!B10:K16"; // ✅ '참고. 구성원 정보' 탭의 특정 범위 지정
    private static final Pattern EMPLOYEE_ID_PATTERN = Pattern.compile("^\\d{10}$"); // ✅ 사번이 숫자로만 이루어졌는지 확인

    /**
     * ✅ Google Sheets에서 유저 정보를 읽고 DB에 저장 + 삭제된 유저 처리
     */
    public void syncUsersFromGoogleSheet() {
        try {
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(SPREADSHEET_ID, RANGE)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                log.warn("⚠️ [WARN] Google Sheets에서 유저 데이터를 찾을 수 없습니다.");
                return;
            }

            // ✅ 현재 DB에 저장된 모든 유저의 사번 목록 조회
            Set<String> existingUserIds = userRepository.findAll().stream()
                    .map(User::getEmployeeId)
                    .collect(Collectors.toSet());

            // ✅ 스프레드시트에서 가져온 유저의 사번 목록
            Set<String> sheetUserIds = new HashSet<>();

            for (List<Object> row : values) {
                if (row.isEmpty()) continue; // ✅ 빈 행 건너뛰기
                if (row.size() < 10) { // ✅ 최소 10개 컬럼이 있어야 유효한 데이터로 판단
                    log.warn("⚠️ [WARN] 데이터 부족으로 인해 유저 정보를 저장할 수 없습니다. {}", row);
                    continue;
                }

                try {
                    // ✅ 사번 검증
                    String employeeId = row.get(0).toString().trim();
                    if (!EMPLOYEE_ID_PATTERN.matcher(employeeId).matches()) {
                        log.warn("⚠️ [WARN] 잘못된 사번 형식으로 인해 유저 정보를 저장할 수 없습니다: {}", employeeId);
                        continue;
                    }

                    sheetUserIds.add(employeeId); // ✅ 현재 동기화할 유저 목록에 추가

                    // ✅ 스프레드시트에서 데이터 가져오기
                    String name = row.get(1).toString().trim(); // 이름
                    LocalDate joinDate = LocalDate.parse(row.get(2).toString().trim()); // 입사일
                    Department department = Department.fromString(row.get(3).toString().trim()); // 소속
                    int part = Integer.parseInt(row.get(4).toString().trim()); // 직무 그룹
                    String levelName = row.get(5).toString().trim(); // 레벨 (ex: "F1-Ⅰ")
                    String loginId = row.get(6).toString().trim(); // 로그인 ID
                    String password = row.get(7).toString().trim(); // 기본 패스워드
                    String changedPw = row.get(8).toString().trim().isEmpty() ? null : row.get(8).toString().trim(); // 변경된 패스워드

                    // ✅ DB의 총 경험치를 유지하고, 스프레드시트의 경험치 값은 반영하지 않음
                    Optional<User> existingUser = userRepository.findByEmployeeId(employeeId);
                    int totalExp = existingUser.map(User::getTotalExp).orElse(0);

                    Role role = Role.USER; // ✅ 기본값 USER

                    // ✅ 레벨 정보 조회
                    Optional<Level> optionalLevel = levelRepository.findByLevelName(levelName);
                    if (optionalLevel.isEmpty()) {
                        log.warn("⚠️ [WARN] 레벨 '{}'에 해당하는 레벨 정보를 찾을 수 없습니다.", levelName);
                        continue; // 레벨이 존재하지 않으면 해당 유저 건너뜀
                    }
                    Level level = optionalLevel.get();

                    // ✅ 기존 유저가 있으면 업데이트, 없으면 새로 생성
                    User user = existingUser
                            .map(existing -> {
                                existing.updateUser(name, joinDate, department, part, level, loginId, password, changedPw, totalExp, role);
                                return existing;
                            })
                            .orElse(User.create(employeeId, name, joinDate, department, part, level, loginId, password, changedPw, totalExp, role));

                    userRepository.save(user);
                    log.info("✅ [INFO] 유저 '{}' 동기화 완료", name);

                } catch (Exception e) {
                    log.error("❌ [ERROR] 유저 데이터 변환 중 오류 발생: {}", row, e);
                }
            }

            // ✅ 스프레드시트에서 삭제된 유저 찾기
            Set<String> usersToDelete = new HashSet<>(existingUserIds);
            usersToDelete.removeAll(sheetUserIds); // ✅ 스프레드시트에서 존재하는 유저는 삭제 대상에서 제외

            if (!usersToDelete.isEmpty()) {
                List<User> usersToBeDeleted = userRepository.findByEmployeeIdIn(usersToDelete);
                userRepository.deleteAll(usersToBeDeleted);
                log.info("🗑 [INFO] {}명의 유저가 삭제됨: {}", usersToDelete.size(), usersToDelete);
            }

            log.info("✅ [INFO] Google Sheets에서 유저 데이터를 성공적으로 동기화했습니다.");
        } catch (IOException e) {
            log.error("❌ [ERROR] Google Sheets에서 유저 데이터를 동기화하는 중 오류 발생", e);
        }
    }
}
