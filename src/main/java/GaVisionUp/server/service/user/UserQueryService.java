package GaVisionUp.server.service.user;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.web.dto.user.UserRequest;
import GaVisionUp.server.web.dto.user.UserResponse;

public interface UserQueryService {

    User login(UserRequest.Login request);

    UserResponse.Information getUserInformation(Long userId);

}