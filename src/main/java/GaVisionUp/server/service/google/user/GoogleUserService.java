package GaVisionUp.server.service.google.user;

import GaVisionUp.server.entity.Level;
import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.Role;
import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.repository.exp.experience.ExperienceRepository;
import GaVisionUp.server.repository.level.LevelRepository;
import GaVisionUp.server.repository.user.UserRepository;
import GaVisionUp.server.service.exp.expbar.ExpBarService;
import GaVisionUp.server.service.exp.experience.ExperienceService;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Year;
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

    private static final String SPREADSHEET_ID = "1jZJTdF5CQrnNioVE_20_IX3X7rB5Zq2psXpmjkcEsLQ"; // ✅ Google 스프레드시트 ID
    private static final String RANGE = "참고. 구성원 정보!B10:K16"; // ✅ '참고. 구성원 정보' 탭의 특정 범위 지정
    private static final Pattern EMPLOYEE_ID_PATTERN = Pattern.compile("^\\d{10}$"); // ✅ 사번이 숫자로만 이루어졌는지 확인
    private static final String RANGE_YEARLY_EXP = "참고. 구성원 정보!L10:V16"; // 연도별 경험치 (L10 ~ V16)
    private static final int START_YEAR = 2013; // 연도별 경험치 시작 (L열 = 2023년 ~ V열 = 2013년)

    /**
     * ✅ Google Sheets와 DB 간 양방향 동기화 수행
     */
    public void syncGoogleSheetWithDatabase() {
        try {
            syncUsersFromGoogleSheet(); // ✅ Google Sheets → DB 동기화
            syncDatabaseToGoogleSheet(); // ✅ DB → Google Sheets 동기화
        } catch (Exception e) {
            log.error("❌ [ERROR] Google Sheets와 DB 동기화 중 오류 발생", e);
        }
    }

    /**
     * ✅ Google Sheets → DB 동기화 (삭제 반영 포함)
     */
    /**
     * ✅ Google Sheets → DB 동기화 (삭제 반영 포함)
     */
    public void syncUsersFromGoogleSheet() {
        try {
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(SPREADSHEET_ID, RANGE)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                log.warn("⚠️ [WARN] Google Sheets에서 유저 데이터를 찾을 수 없습니다. (전체 삭제 가능성 있음)");
                values = new ArrayList<>(); // ✅ 빈 리스트로 설정하여 usersToDelete가 기존 유저 목록을 유지하도록 함
            }

            // ✅ 현재 DB에 저장된 모든 유저의 사번 목록 조회
            Set<String> existingUserIds = userRepository.findAll().stream()
                    .map(User::getEmployeeId)
                    .collect(Collectors.toSet());

            // ✅ 스프레드시트에서 가져온 유저의 사번 목록
            Set<String> sheetUserIds = new HashSet<>();

            for (List<Object> row : values) {
                if (row.isEmpty()) continue;
                if (row.size() < 10) {
                    log.warn("⚠️ [WARN] 데이터 부족으로 인해 유저 정보를 저장할 수 없습니다. {}", row);
                    continue;
                }

                try {
                    String employeeId = row.get(0).toString().trim();
                    if (!EMPLOYEE_ID_PATTERN.matcher(employeeId).matches()) {
                        log.warn("⚠️ [WARN] 잘못된 사번 형식으로 인해 유저 정보를 저장할 수 없습니다: {}", employeeId);
                        continue;
                    }

                    sheetUserIds.add(employeeId);

                    // ✅ 스프레드시트에서 데이터 가져오기
                    String name = row.get(1).toString().trim();
                    LocalDate joinDate = LocalDate.parse(row.get(2).toString().trim());
                    Department department = Department.fromString(row.get(3).toString().trim());
                    int part = Integer.parseInt(row.get(4).toString().trim());
                    String levelName = row.get(5).toString().trim();
                    String loginId = row.get(6).toString().trim();
                    String password = row.get(7).toString().trim();
                    String changedPw = row.get(8).toString().trim().isEmpty() ? null : row.get(8).toString().trim();

                    Optional<User> existingUser = userRepository.findByEmployeeId(employeeId);
                    int totalExp = existingUser.map(User::getTotalExp).orElse(0);

                    Role role = Role.USER;

                    Optional<Level> optionalLevel = levelRepository.findByLevelName(levelName);
                    if (optionalLevel.isEmpty()) {
                        log.warn("⚠️ [WARN] 레벨 '{}'에 해당하는 레벨 정보를 찾을 수 없습니다.", levelName);
                        continue;
                    }
                    Level level = optionalLevel.get();

                    // ✅ 기존 유저가 있으면 업데이트, 없으면 새로 생성
                    User user = existingUser
                            .map(existing -> {
                                existing.updateUser(name, joinDate, department, part, level, loginId, password, changedPw, totalExp, role);
                                return existing;
                            })
                            .orElseGet(() -> {
                                User newUser = User.create(employeeId, name, joinDate, department, part, level, loginId, password, changedPw, totalExp, role);
                                userRepository.save(newUser);

                                // ✅ 새로운 유저의 경우 ExpBar 생성
                                ExpBar expBar = expBarService.getOrCreateExpBarByUserId(newUser.getId());
                                log.info("✅ [INFO] 신규 유저 '{}'의 ExpBar 생성 완료", name);
                                return newUser;
                            });

                    userRepository.save(user);
                    log.info("✅ [INFO] 유저 '{}' 동기화 완료", name);

                } catch (Exception e) {
                    log.error("❌ [ERROR] 유저 데이터 변환 중 오류 발생: {}", row, e);
                }
            }

            // ✅ 삭제된 유저 처리 (Google Sheets에서 사라진 유저를 DB에서 삭제)
            Set<String> usersToDelete = new HashSet<>(existingUserIds);
            usersToDelete.removeAll(sheetUserIds);

            if (!usersToDelete.isEmpty()) {
                List<User> usersToBeDeleted = userRepository.findByEmployeeIdIn(usersToDelete);
                userRepository.deleteAll(usersToBeDeleted);
                log.info("🗑 [INFO] {}명의 유저가 삭제됨: {}", usersToDelete.size(), usersToDelete);
            } else {
                log.info("✅ [INFO] 삭제할 유저가 없습니다.");
            }

        } catch (IOException e) {
            log.error("❌ [ERROR] Google Sheets에서 유저 데이터를 동기화하는 중 오류 발생", e);
        }
    }

    /**
     * ✅ DB → Google Sheets 동기화
     */
    public void syncDatabaseToGoogleSheet() {
        try {
            List<User> users = userRepository.findAll();

            // ✅ Google Sheets에 저장할 데이터 변환 (List<List<Object>>)
            List<ArrayList<? extends Serializable>> userData = users.stream()
                    .map(user -> new ArrayList<>(Arrays.asList( // ✅ 명확한 타입 캐스팅을 위해 ArrayList<Object> 사용
                            user.getEmployeeId(),  // 사번
                            user.getName(),  // 이름
                            user.getJoinDate().toString(),  // 입사일
                            user.getDepartment().getValue(),  // 소속
                            user.getPart(),  // 직무 그룹
                            user.getLevel().getLevelName(),  // 레벨
                            user.getLoginId(),  // 로그인 ID
                            user.getPassword(),  // 패스워드
                            user.getChangedPW() != null ? user.getChangedPW() : "",  // 변경된 패스워드 (없으면 빈 문자열)
                            user.getTotalExp()  // 총 경험치 (정수)
                    )))
                    .collect(Collectors.toList()); // ✅ 최종적으로 List<List<Object>> 형태로 변환

            // ✅ Google Sheets 데이터 초기화 (기존 데이터 삭제)
            sheetsService.spreadsheets().values()
                    .clear(SPREADSHEET_ID, RANGE, new ClearValuesRequest())
                    .execute();

            // ✅ Google Sheets에 새로운 데이터 입력
            sheetsService.spreadsheets().values()
                    .update(SPREADSHEET_ID, RANGE, new ValueRange().setValues((List<List<Object>>) (List<?>) userData)) // ✅ 강제 타입 변환
                    .setValueInputOption("RAW")
                    .execute();

            // ✅ 연도별 경험치 데이터 생성
            List<List<Object>> yearlyExpData = new ArrayList<>();
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);

            for (User user : users) {
                Map<Integer, Integer> experienceMap = experienceService.getYearlyTotalExperience(user.getId(), START_YEAR, currentYear);
                List<Object> yearlyExp = IntStream.rangeClosed(START_YEAR, currentYear-2)
                        .mapToObj(year -> experienceMap.getOrDefault(year, 0)) // 연도별 경험치 값이 없으면 0
                        .collect(Collectors.toList());

                yearlyExpData.add(yearlyExp);
            }

            // ✅ Google Sheets 연도별 경험치 업데이트 (L10:V)
            sheetsService.spreadsheets().values()
                    .update(SPREADSHEET_ID, RANGE_YEARLY_EXP, new ValueRange().setValues(yearlyExpData))
                    .setValueInputOption("RAW")
                    .execute();

            log.info("✅ [INFO] DB 데이터를 Google Sheets에 성공적으로 동기화했습니다.");

        } catch (IOException e) {
            log.error("❌ [ERROR] DB 데이터를 Google Sheets에 동기화하는 중 오류 발생", e);
        }
    }

}
