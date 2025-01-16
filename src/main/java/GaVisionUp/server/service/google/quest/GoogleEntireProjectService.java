package GaVisionUp.server.service.google.quest;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.entity.quest.EntireProject;
import GaVisionUp.server.repository.exp.experience.ExperienceRepository;

import GaVisionUp.server.repository.quest.entire.EntireProjectRepository;
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
public class GoogleEntireProjectService {

    private final EntireProjectRepository entireProjectRepository;
    private final UserRepository userRepository;
    private final ExperienceRepository experienceRepository;
    private final Sheets sheetsService;

    @Value("${google.sheets.spreadsheet-id}") // ✅ YML에서 스프레드시트 ID 가져오기
    private String spreadsheetId;

    private static final String RANGE_PROJECTS = "참고. 전사 프로젝트!B8:H"; // ✅ 전사 프로젝트 데이터 범위

    /**
     * ✅ Google Sheets에서 전사 프로젝트 데이터를 읽어와 DB에 저장
     */
    public void syncEntireProjects() {
        try {
            // ✅ 데이터 가져오기
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, RANGE_PROJECTS)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                return;
            }

            for (List<Object> row : values) {
                // ✅ 필수 값 체크 (사번, 대상자, 전사 프로젝트명, 부여 경험치)
                if (row.size() < 5 || row.get(2) == null || row.get(3) == null || row.get(4) == null || row.get(5) == null) {
                    continue;
                }

                try {
                    // ✅ 데이터 매핑
                    String employeeId = row.get(2).toString().trim();
                    String projectName = row.get(4).toString().trim();
                    int newGrantedExp = Integer.parseInt(row.get(5).toString().trim()); // 새로 저장할 경험치
                    String note = (row.size() > 6 && row.get(6) != null) ? row.get(6).toString().trim() : "";
                    LocalDate assignedDate = LocalDate.now();

                    // ✅ 유저 조회
                    Optional<User> userOpt = userRepository.findByEmployeeId(employeeId);
                    if (userOpt.isEmpty()) {
                        continue;
                    }
                    User user = userOpt.get();

                    // ✅ 프로젝트 중복 확인
                    Optional<EntireProject> existingProjectOpt = entireProjectRepository.findByUserAndProjectNameAndAssignedDate(
                            user, projectName, assignedDate);

                    int previousGrantedExp = 0; // 기존 경험치
                    if (existingProjectOpt.isPresent()) {
                        EntireProject existingProject = existingProjectOpt.get();
                        previousGrantedExp = existingProject.getGrantedExp(); // 기존 경험치 값 가져오기
                        // ✅ 프로젝트 업데이트
                        existingProject.updateProject(projectName, newGrantedExp, note, assignedDate);
                        entireProjectRepository.save(existingProject);
                    } else {

                        EntireProject entireProject = EntireProject.create(
                                user, projectName, newGrantedExp, note, assignedDate);
                        entireProjectRepository.save(entireProject);
                    }

                    // ✅ 경험치 변화량 계산 및 저장
                    int experienceDiff = newGrantedExp - previousGrantedExp;
                    if (experienceDiff != 0) {
                        if (newGrantedExp == 0) {
                            Experience newExperience = new Experience(user, ExpType.LEADER_QUEST, experienceDiff);
                            experienceRepository.edit(newExperience);
                        }
                    } else if(experienceDiff == 0){
                        continue;
                    }
                   else {
                        Experience experience = new Experience(user, ExpType.ENTIRE_PROJECT, experienceDiff);
                        experienceRepository.save(experience);
                    }

                    // ✅ 유저 총 경험치 업데이트
                    userRepository.save(user);

                } catch (Exception e) {
                    log.error("❌ [ERROR] 전사 프로젝트 처리 중 오류 발생: {}", row, e);
                }
            }
        } catch (IOException e) {
            log.error("❌ [ERROR] Google Sheets 데이터를 읽는 중 오류 발생", e);
        } catch (Exception e) {
            log.error("❌ [ERROR] 전사 프로젝트 동기화 중 오류 발생", e);
        }
    }
}
