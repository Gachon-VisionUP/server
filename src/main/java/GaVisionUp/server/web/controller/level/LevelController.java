package GaVisionUp.server.web.controller.level;

import GaVisionUp.server.entity.Level;
import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.JobGroup;
import GaVisionUp.server.service.level.LevelService;
import GaVisionUp.server.service.user.UserQueryService;
import GaVisionUp.server.web.dto.level.LevelResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/levels")
@RequiredArgsConstructor
public class LevelController {

    private final LevelService levelService;
    private final UserQueryService userQueryService;

    // ✅ 특정 직군(JobGroup)의 레벨 리스트 조회
    @GetMapping("/{jobGroup}")
    public ResponseEntity<List<Level>> getLevelsByJobGroup(@PathVariable JobGroup jobGroup) {
        return ResponseEntity.ok(levelService.getLevelsByJobGroup(jobGroup));
    }

    // ✅ 특정 유저의 총 경험치에 따른 현재 레벨 조회
    @GetMapping("/user-level")
    public ResponseEntity<Level> getUserLevel(@RequestParam JobGroup jobGroup, @RequestParam int totalExp) {
        return ResponseEntity.ok(levelService.getLevelByExp(jobGroup, totalExp));
    }

    // ✅ 전체 레벨 정보 조회 API (유저 직군 기반 필터링)
    @GetMapping("/group/all")
    @Operation(summary = "전체 레벨 정보 조회 API", description = "유저 직군을 기반으로 필터링하여 전체 레벨 정보를 조회합니다.")
    public ResponseEntity<Map<String, List<LevelResponse>>> getAllLevels(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(null);
        }

        // ✅ 사용자 정보 가져오기
        User user = userQueryService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // ✅ 사용자의 직군 기반으로 레벨 목록 조회
        JobGroup jobGroup = user.getLevel().getJobGroup();
        List<Level> levels = levelService.getLevelsByJobGroup(jobGroup);

        // ✅ 응답 변환
        List<LevelResponse> levelResponses = levels.stream()
                .map(LevelResponse::new)
                .toList();

        // ✅ 특정 직군만 반환하도록 Map 형식으로 리턴
        Map<String, List<LevelResponse>> response = Map.of(jobGroup.name(), levelResponses);

        return ResponseEntity.ok(response);
    }
}
