package GaVisionUp.server.web.controller.notification;

import GaVisionUp.server.global.base.ApiResponse;
import GaVisionUp.server.service.user.UserCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/push-tokens")
@RequiredArgsConstructor
public class PushTokenController {

    private final UserCommandService userCommandService;

    @PostMapping("/{userId}")
    public ApiResponse<String> savePushToken(
            @PathVariable Long userId,
            @RequestBody String expoPushToken) {

        userCommandService.updatePushToken(userId, expoPushToken);
        return ApiResponse.onSuccess("Push token saved successfully!");
    }
}
