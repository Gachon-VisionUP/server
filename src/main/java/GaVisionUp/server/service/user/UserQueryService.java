package GaVisionUp.server.service.user;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.web.dto.UserRequest;
import GaVisionUp.server.web.dto.UserResponse;

import java.util.Optional;

public interface UserQueryService {

    User login(UserRequest.Login request);

    UserResponse.Information getUserInformation(Long userId);

}
