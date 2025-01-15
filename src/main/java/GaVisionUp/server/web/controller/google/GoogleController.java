package GaVisionUp.server.web.controller.google;

import GaVisionUp.server.service.google.user.GoogleUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sheets")
@RequiredArgsConstructor
public class GoogleController {

    private final GoogleUserService googleUserService;

    /**
     * ✅ Google Sheets에서 유저 데이터 동기화 API
     */
    @PostMapping("/sync-users")
    public ResponseEntity<String> syncUsers() {
        googleUserService.syncUsersFromGoogleSheet();
        return ResponseEntity.ok("✅ Google Sheets에서 유저 데이터를 동기화했습니다.");
    }
}
