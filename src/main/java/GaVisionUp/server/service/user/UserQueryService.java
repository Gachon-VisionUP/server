package GaVisionUp.server.service.user;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.web.dto.user.UserRequest;
import GaVisionUp.server.web.dto.user.UserResponse;

import java.util.List;

public interface UserQueryService {

    User login(UserRequest.Login request);

    UserResponse.Information getUserInformation(Long userId);

    List<String> getAllExpoPushTokens();

}