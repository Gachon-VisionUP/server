package GaVisionUp.server.service.google;

import GaVisionUp.server.entity.Level;
import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.Role;
import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.repository.exp.expbar.ExpBarRepository;
import GaVisionUp.server.repository.level.LevelRepository;
import GaVisionUp.server.repository.user.UserRepository;
import GaVisionUp.server.service.exp.expbar.ExpBarService;
import GaVisionUp.server.service.exp.experience.ExperienceService;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleUserService {

    private final UserRepository userRepository;
    private final LevelRepository levelRepository;
    private final ExperienceService experienceService;
    private final ExpBarService expBarService;
    private final Sheets sheetsService;

    @Value("${google.sheets.spreadsheet-id}") // ✅ YML에서 스프레드시트 ID 주입
    private String spreadsheetId;
    private static final String RANGE = "참고. 구성원 정보!B10:K16"; // ✅ '참고. 구성원 정보' 탭의 특정 범위 지정
    private static final Pattern EMPLOYEE_ID_PATTERN = Pattern.compile("^\\d{10}$"); // ✅ 사번이 숫자로만 이루어졌는지 확인
    private static final String RANGE_YEARLY_EXP = "참고. 구성원 정보!L10:X16"; // 연도별 경험치 (L10 ~ V16)
    private static final int START_YEAR = 2025; // 연도별 경험치 시작 (L열 = 2023년 ~ V열 = 2013년)
    private final ExpBarRepository expBarRepository;

    /**
     * ✅ Google Sheets → DB 동기화 (삭제 반영 포함)
     */
    public void syncUsersFromGoogleSheet() {
        try {
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, RANGE)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                log.warn("⚠️ [WARN] Google Sheets에서 유저 데이터를 찾을 수 없습니다. (전체 삭제 가능성 있음)");
                values = new ArrayList<>();
            }

            Set<String> existingUserIds = userRepository.findAll().stream()
                    .map(User::getEmployeeId)
                    .collect(Collectors.toSet());

            Set<String> sheetUserIds = new HashSet<>();

            for (List<Object> row : values) {
                if (row.isEmpty() || row.size() < 10) {
                    continue;
                }

                try {
                    String employeeId = row.get(0).toString().trim();
                    if (!EMPLOYEE_ID_PATTERN.matcher(employeeId).matches()) {
                        continue;
                    }

                    sheetUserIds.add(employeeId);

                    String name = row.get(1).toString().trim();
                    LocalDate joinDate = LocalDate.parse(row.get(2).toString().trim());
                    Department department = Department.fromString(row.get(3).toString().trim());
                    int part = Integer.parseInt(row.get(4).toString().trim());
                    String levelName = row.get(5).toString().trim();
                    String loginId = row.get(6).toString().trim();
                    String password = row.get(7).toString().trim();
                    String changedPw = row.get(8).toString().trim().isEmpty() ? null : row.get(8).toString().trim();

                    // ✅ 총 경험치 값 처리 (쉼표 제거 후 int로 변환)
                    final int totalExp = !row.get(9).toString().trim().isEmpty()
                            ? Integer.parseInt(row.get(9).toString().trim().replace(",", ""))
                            : 0;

                    Optional<User> existingUser = userRepository.findByEmployeeId(employeeId);
                    Role role = Role.USER;

                    Optional<Level> optionalLevel = levelRepository.findByLevelName(levelName);
                    if (optionalLevel.isEmpty()) {
                        continue;
                    }
                    Level level = optionalLevel.get();

                    User user;

                    // ✅ Optional 처리 수정
                    if (existingUser.isPresent()) {
                        User existing = existingUser.get();
                        existing.updateUser(name, joinDate, department, part, level, loginId, password, changedPw, totalExp, role);
                        user = existing;
                    } else {
                        User newUser = User.create(employeeId, name, joinDate, department, part, level, loginId, password, changedPw, totalExp, role);
                        userRepository.save(newUser);

                        // ExpBar 생성
                        ExpBar expBar = expBarService.getOrCreateExpBarByUserId(newUser.getId());
                        log.info("✅ [INFO] 신규 유저 '{}'의 ExpBar 생성 완료", name);

                        user = newUser;
                    }
                    userRepository.save(user);

                } catch (Exception e) {
                    log.error("❌ [ERROR] 유저 데이터 변환 중 오류 발생: {}", row, e);
                }
            }

            Set<String> usersToDelete = new HashSet<>(existingUserIds);
            usersToDelete.removeAll(sheetUserIds);

            if (!usersToDelete.isEmpty()) {
                List<User> usersToBeDeleted = userRepository.findByEmployeeIdIn(usersToDelete);
                userRepository.deleteAll(usersToBeDeleted);
            } else {
                log.info("✅ [INFO] 삭제할 유저가 없습니다.");
            }

        } catch (IOException e) {
            log.error("❌ [ERROR] Google Sheets에서 유저 데이터를 동기화하는 중 오류 발생", e);
        }
    }

    public void syncDatabaseToGoogleSheet() {
        try {
            List<User> users = userRepository.findAll();

            // ✅ Google Sheets에서 기존 데이터를 가져와 번호와 총 경험치 유지
            ValueRange existingDataResponse = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, RANGE)
                    .execute();

            List<List<Object>> existingData = existingDataResponse.getValues();
            Map<String, List<Object>> existingUserDataMap = new HashMap<>();

            if (existingData != null) {
                for (List<Object> row : existingData) {
                    if (row.size() > 0) {
                        existingUserDataMap.put(row.get(0).toString().trim(), row); // 사번을 키로 저장
                    }
                }
            }

            // ✅ 새로운 사용자 데이터를 준비
            List<ArrayList<Object>> userData = users.stream()
                    .map(user -> {
                        List<Object> existingRow = existingUserDataMap.get(user.getEmployeeId());
                        Object totalExp = (existingRow != null && existingRow.size() > 9) ? existingRow.get(9) : 0; // 기존 총 경험치 값 유지

                        return new ArrayList<>(Arrays.asList(
                                user.getEmployeeId(),
                                user.getName(),
                                user.getJoinDate().toString(),
                                user.getDepartment().getValue(),
                                user.getPart(),
                                user.getLevel().getLevelName(),
                                user.getLoginId(),
                                user.getPassword(),
                                user.getChangedPW() != null ? user.getChangedPW() : "",
                                totalExp // 총 경험치 유지
                        ));
                    })
                    .collect(Collectors.toList());


            // ✅ 연도별 경험치 데이터 생성
            List<List<Object>> yearlyExpData = new ArrayList<>();
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);

            for (User user : users) {
                Map<Integer, Integer> experienceMap = experienceService.getYearlyTotalExperience(user.getId(), START_YEAR, currentYear);
                List<Object> yearlyExp = IntStream.rangeClosed(START_YEAR, currentYear)
                        .mapToObj(year -> experienceMap.getOrDefault(year, 0))
                        .collect(Collectors.toList());

                yearlyExpData.add(yearlyExp);
            }

            // ✅ 연도별 경험치만 업데이트
            sheetsService.spreadsheets().values()
                    .update(spreadsheetId, RANGE_YEARLY_EXP, new ValueRange().setValues(yearlyExpData))
                    .setValueInputOption("RAW")
                    .execute();

        } catch (IOException e) {
            log.error("❌ [ERROR] DB 데이터를 Google Sheets에 동기화하는 중 오류 발생", e);
        }
    }
}
