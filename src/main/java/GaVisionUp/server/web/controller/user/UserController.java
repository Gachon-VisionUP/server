package GaVisionUp.server.web.controller.user;

import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.global.base.ApiResponse;
import GaVisionUp.server.service.user.UserCommandService;
import GaVisionUp.server.service.user.UserQueryService;
import GaVisionUp.server.web.dto.UserRequest;
import GaVisionUp.server.web.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @GetMapping("/{userId}/information")
    public ApiResponse<UserResponse.Information> getUserInformation(
            @PathVariable Long userId) {
        return ApiResponse.onSuccess(userQueryService.getUserInformation(userId));
    }

    @PutMapping("/{userId}/information")
    public ApiResponse<UserResponse.UpdateInformation> updateInformation(
            @PathVariable Long userId,
            @RequestBody UserRequest.Update request){
        return ApiResponse.onSuccess(userCommandService.updateInformation(userId, request));
    }
}
