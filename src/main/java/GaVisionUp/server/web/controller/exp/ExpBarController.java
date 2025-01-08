package GaVisionUp.server.web.controller.exp;

import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.service.exp.expbar.ExpBarService;
import GaVisionUp.server.web.dto.ExpBarResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exp-bar")
@RequiredArgsConstructor
public class ExpBarController {
    private final ExpBarService expBarService;

    // 최초 회원 추가 시에 경험치 바 생성
    @PostMapping("/create")
    public ResponseEntity<ExpBar> createExpBar(@RequestBody ExpBar expBar) {
        return ResponseEntity.ok(expBarService.createExpBar(expBar));
    }

    // 특정 사원의 경험치 바 조회
    @GetMapping("/{userId}")
    public ResponseEntity<ExpBarResponse> getExpBar(@PathVariable Long userId) {
        ExpBar expBar = expBarService.getExpBarByUserId(userId);
        return ResponseEntity.ok(new ExpBarResponse(expBar));  // ✅ DTO 변환
    }

    // 경험치 추가
    @PostMapping("/{userId}/add-exp")
    public ResponseEntity<ExpBarResponse> addExperience(@PathVariable Long userId, @RequestParam int exp) {
        ExpBar updatedExpBar = expBarService.addExperience(userId, exp);
        return ResponseEntity.ok(new ExpBarResponse(updatedExpBar));  // ✅ DTO 변환
    }
}
