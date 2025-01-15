package GaVisionUp.server.web.controller;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.entity.quest.leader.LeaderQuest;
import GaVisionUp.server.entity.quest.leader.LeaderQuestCondition;
import GaVisionUp.server.repository.quest.leader.LeaderQuestRepository;
import GaVisionUp.server.service.exp.expbar.ExpBarService;
import GaVisionUp.server.service.exp.experience.ExperienceService;
import GaVisionUp.server.service.quest.leader.LeaderQuestService;
import GaVisionUp.server.service.user.UserQueryService;
import GaVisionUp.server.web.dto.HomeQuestInfoResponse;
import GaVisionUp.server.web.dto.HomeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {
    private final UserQueryService userQueryService;
    private final LeaderQuestService leaderQuestService;
    private final LeaderQuestRepository leaderQuestRepository;
    private final ExperienceService experienceService;
    private final ExpBarService expBarService;

    @Value("${server.url}") // 서버 URL (예: http://localhost:8080)
    private String serverUrl;

    // ✅ 홈 데이터 조회 (유저 이름, 현재 레벨, 리더 부여 퀘스트 목록 및 평가 등급)
    // ✅ 홈 데이터 조회 (최신 경험치, 총 경험치, 퀘스트 목록)
    @GetMapping
    @Operation(summary = "홈으로 이동 API", description = "홈 데이터를 조회합니다.(이름, 레벨, 퀘스트 목록 및 평가 등급, 최신 경험치, 총 경험치, 퀘스트 목록)")
    public ResponseEntity<HomeResponse> getHomeData(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long sessionUserId) {
        if (sessionUserId == null) {
            log.warn("⚠️ [WARN] 세션에 로그인된 사용자가 없습니다.");
            return ResponseEntity.badRequest().build();
        }

        // ✅ 사용자 정보 가져오기
        User user = userQueryService.getUserById(sessionUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // ✅ 현재 레벨 조회
        String currentLevel = user.getLevel().getLevelName();

        // ✅ 총 경험치 조회
        int totalExp = user.getTotalExp();
        String imageURL = serverUrl + "/images/" + user.getProfileImageUrl() + ".png";

        // ✅ 해당 유저의 ExpBar 확인 및 자동 생성
        ExpBar expBar = expBarService.getOrCreateExpBarByUserId(sessionUserId);

        // ✅ 최신 경험치 조회
        Optional<Experience> latestExperienceOpt = experienceService.getLatestExperienceByUserId(sessionUserId);
        int latestExp = latestExperienceOpt.map(Experience::getExp).orElse(0); // 기본값 0

        // ✅ 퀘스트 조건 목록 조회 (유저의 소속 기반)
        List<LeaderQuestCondition> questConditions = leaderQuestService.getConditionsByUserId(sessionUserId);

        // ✅ 각 퀘스트별 최근 경험치 조회
        List<HomeQuestInfoResponse> questInfos = questConditions.stream()
                .map(condition -> {
                    // ✅ 최신 리더 퀘스트 기록 조회
                    Optional<LeaderQuest> latestQuest = leaderQuestRepository.findTopByUserIdAndQuestName(sessionUserId, condition.getQuestName());

                    // ✅ 퀘스트 수행 기록이 없으면 경험치 없이 퀘스트명만 반환
                    return latestQuest.map(quest -> new HomeQuestInfoResponse(quest.getQuestName(), quest.getGrantedExp()))
                            .orElse(new HomeQuestInfoResponse(condition.getQuestName(), null));
                })
                .collect(Collectors.toList());

        log.info("✅ [INFO] 홈 데이터 조회 - 사용자: {}, 레벨: {}, 총 경험치: {}", user.getName(), currentLevel, totalExp);

        return ResponseEntity.ok(new HomeResponse(user.getName(), currentLevel, totalExp, latestExp,imageURL,questInfos));
    }
}
