package GaVisionUp.server.web.controller.exp;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.repository.user.UserRepository;
import GaVisionUp.server.service.exp.expbar.ExpBarService;
import GaVisionUp.server.web.dto.ExpBarRequest;
import GaVisionUp.server.web.dto.ExpBarResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exp-bar")
@RequiredArgsConstructor
public class ExpBarController {
    private final ExpBarService expBarService;

    private final UserRepository userRepository;

    // 최초 회원 추가 시에 경험치 바 생성
    @PostMapping("/create")
    public ResponseEntity<ExpBar> createExpBar(@RequestBody ExpBarRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원입니다.")); // ✅ userId 검증

        ExpBar expBar = new ExpBar(user); // ✅ ExpBar 생성 시 유저 레벨 자동 반영
        ExpBar savedExpBar = expBarService.createExpBar(expBar);

        return ResponseEntity.ok(savedExpBar);
    }


    // 특정 사원의 경험치 바 조회
    @GetMapping("/{userId}")
    public ResponseEntity<ExpBarResponse> getExpBar(@PathVariable Long userId) {
        ExpBar expBar = expBarService.getExpBarByUserId(userId);
        return ResponseEntity.ok(new ExpBarResponse(expBar));  // ✅ DTO 변환
    }
}
