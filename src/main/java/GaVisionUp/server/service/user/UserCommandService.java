package GaVisionUp.server.service.user;

import GaVisionUp.server.web.dto.user.UserRequest;
import GaVisionUp.server.web.dto.user.UserResponse;

public interface UserCommandService {

    UserResponse.UpdateInformation updateInformation(Long userId, UserRequest.Update request);
}