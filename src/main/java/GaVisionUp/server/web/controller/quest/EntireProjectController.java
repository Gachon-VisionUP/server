package GaVisionUp.server.web.controller.quest;

import GaVisionUp.server.service.quest.EntireProjectService;
import GaVisionUp.server.web.dto.quest.EntireProjectRequest;
import GaVisionUp.server.web.dto.quest.EntireProjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/entire-project")
@RequiredArgsConstructor
public class EntireProjectController {

    private final EntireProjectService entireProjectService;

    // ✅ 전사 프로젝트 참여 기록 추가 (경험치 자동 부여)
    @PostMapping("/assign")
    public ResponseEntity<EntireProjectResponse> assignEntireProject(@RequestBody EntireProjectRequest request) {
        EntireProjectResponse response = new EntireProjectResponse(
                entireProjectService.createEntireProject(
                        request.getUserId(),
                        request.getProjectName(),
                        request.getGrantedExp(),
                        request.getNote(),
                        request.getAssignedDate()
                )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // ✅ 201 Created
    }

    // ✅ 특정 유저의 프로젝트 참여 기록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EntireProjectResponse>> getUserEntireProjects(@PathVariable Long userId) {
        List<EntireProjectResponse> response = entireProjectService.getEntireProjectsByUser(userId)
                .stream().map(EntireProjectResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // ✅ 월별 전사 프로젝트 참여 기록 조회
    @GetMapping("/month/{year}/{month}")
    public ResponseEntity<List<EntireProjectResponse>> getEntireProjectsByMonth(@PathVariable int year, @PathVariable int month) {
        List<EntireProjectResponse> response = entireProjectService.getEntireProjectsByMonth(year, month)
                .stream().map(EntireProjectResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // ✅ 특정 날짜의 전사 프로젝트 참여 기록 조회
    @GetMapping("/date/{date}")
    public ResponseEntity<List<EntireProjectResponse>> getEntireProjectsByDate(@PathVariable LocalDate date) {
        List<EntireProjectResponse> response = entireProjectService.getEntireProjectsByDate(date)
                .stream().map(EntireProjectResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // ✅ 전체 전사 프로젝트 참여 기록 조회
    @GetMapping("/all")
    public ResponseEntity<List<EntireProjectResponse>> getAllEntireProjects() {
        List<EntireProjectResponse> response = entireProjectService.getAllEntireProjects()
                .stream().map(EntireProjectResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
