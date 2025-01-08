package GaVisionUp.server.web.controller.exp;

import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.service.exp.experience.ExperienceService;
import GaVisionUp.server.web.dto.ExperienceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/experience")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;

    // ✅ 경험치 추가
    @PostMapping("/add")
    public ResponseEntity<ExperienceResponse> addExperience(
            @RequestParam Long userId,
            @RequestParam ExpType expType,
            @RequestParam int exp) {
        Experience experience = experienceService.addExperience(userId, expType, exp);
        return ResponseEntity.ok(new ExperienceResponse(
                experience.getId(),
                experience.getUser().getId(),
                experience.getExpType(),
                experience.getExp(),
                experience.getObtainedDate()
        ));
    }

    // ✅ 특정 경험치 조회
    @GetMapping("/{id}")
    public ResponseEntity<ExperienceResponse> getExperienceById(@PathVariable Long id) {
        Experience experience = experienceService.getExperienceById(id)
                .orElseThrow(() -> new IllegalArgumentException("경험치 기록을 찾을 수 없습니다."));
        return ResponseEntity.ok(new ExperienceResponse(
                experience.getId(),
                experience.getUser().getId(),
                experience.getExpType(),
                experience.getExp(),
                experience.getObtainedDate()
        ));
    }

    // ✅ 특정 유저의 모든 경험치 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExperienceResponse>> getExperiencesByUserId(@PathVariable Long userId) {
        List<Experience> experiences = experienceService.getExperiencesByUserId(userId);
        List<ExperienceResponse> response = experiences.stream()
                .map(exp -> new ExperienceResponse(
                        exp.getId(),
                        exp.getUser().getId(),
                        exp.getExpType(),
                        exp.getExp(),
                        exp.getObtainedDate()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
