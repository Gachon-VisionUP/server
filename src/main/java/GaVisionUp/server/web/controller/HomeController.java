package GaVisionUp.server.web.controller;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.service.exp.experience.ExperienceService;
import GaVisionUp.server.service.user.UserQueryService;
import GaVisionUp.server.web.dto.HomeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final ExperienceService experienceService;
    private final UserQueryService userQueryService;

    // ✅ 홈 데이터 조회 (최신 획득 경험치, 총 경험치)
    @GetMapping
    public ResponseEntity<HomeResponse> getHomeData(@SessionAttribute(name = "userId", required = false) Long sessionUserId) {
        if (sessionUserId == null) {
            log.warn("⚠️ [WARN] 세션에 로그인된 사용자가 없습니다.");
            return ResponseEntity.badRequest().build();
        }

        // ✅ 사용자 정보 가져오기
        User user = userQueryService.getUserById(sessionUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // ✅ 최신 경험치 조회
        Optional<Experience> latestExperienceOpt = experienceService.getLatestExperienceByUserId(sessionUserId);
        int latestExp = latestExperienceOpt.map(Experience::getExp).orElse(0); // 기본값 0

        // ✅ 총 경험치 조회
        int totalExp = user.getTotalExp();

        log.info("✅ [INFO] 홈 데이터 조회 - 사용자: {}, 최신 획득 경험치: {}, 총 경험치: {}", user.getName(), latestExp, totalExp);

        return ResponseEntity.ok(new HomeResponse(latestExp, totalExp));
    }
}
