package GaVisionUp.server.web.controller.user;

import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.global.base.ApiResponse;
import GaVisionUp.server.service.user.UserQueryService;
import GaVisionUp.server.web.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserQueryService userQueryService;

    @GetMapping("/{userId}/information")
    public ApiResponse<UserResponse.Information> getUserInformation(@PathVariable Long userId) {
        return ApiResponse.onSuccess(userQueryService.getUserInformation(userId));
    }
}
