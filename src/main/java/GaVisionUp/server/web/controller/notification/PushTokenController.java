package GaVisionUp.server.web.controller.notification;

import GaVisionUp.server.global.base.ApiResponse;
import GaVisionUp.server.service.user.UserCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/push-tokens")
@RequiredArgsConstructor
public class PushTokenController {

    private final UserCommandService userCommandService;

    @PostMapping("/{userId}")
    @Operation(summary = "푸쉬 토큰 수정 API", description = "프론트에서 push token 수정할 일 있을 때 사용하면 될 듯..?")
    @Parameters({
            @Parameter(name = "userId", description = "수정할 구성원의 id")
    })
    public ApiResponse<String> savePushToken(
            @PathVariable Long userId,
            @RequestBody String expoPushToken) {

        userCommandService.updatePushToken(userId, expoPushToken);
        return ApiResponse.onSuccess("Push token saved successfully!");
    }
}
