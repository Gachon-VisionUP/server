package GaVisionUp.server.service.user;

import GaVisionUp.server.web.dto.UserRequest;
import GaVisionUp.server.web.dto.UserResponse;

public interface UserCommandService {

    UserResponse.UpdateInformation updateInformation(Long userId, UserRequest.Update request);
}
