package GaVisionUp.server.web.controller.exp;

import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.service.exp.ExpBarService;
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
    public ResponseEntity<ExpBar> getExpBar(@PathVariable int userId) {
        return ResponseEntity.ok(expBarService.getExpBarByUserId(userId));
    }

    // 총 경험치에 새로 경험치 추가
    @PostMapping("/{userId}/add-exp")
    public ResponseEntity<ExpBar> addExperience(@PathVariable int userId, @RequestParam int exp) {
        return ResponseEntity.ok(expBarService.addExperience(userId, exp));
    }
}
