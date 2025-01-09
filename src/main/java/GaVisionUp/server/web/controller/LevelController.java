package GaVisionUp.server.web.controller;

import GaVisionUp.server.entity.Level;
import GaVisionUp.server.entity.enums.JobGroup;
import GaVisionUp.server.service.level.LevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/levels")
@RequiredArgsConstructor
public class LevelController {

    private final LevelService levelService;

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
}
