package GaVisionUp.server.service.user;

import GaVisionUp.server.web.dto.user.UserRequest;
import GaVisionUp.server.web.dto.user.UserResponse;

public interface UserCommandService {

    UserResponse.UpdateInformation updateInformation(Long userId, UserRequest.Update request);

    void updatePushToken(Long userId, String pushToken);

    UserResponse.Create userCreate(Long userId, UserRequest.Create request);

    UserResponse.UpdateInformation updateUserInfo(Long userId, Long targetId, UserRequest.UpdateUserInfo request);
}