package GaVisionUp.server.service.user;

import GaVisionUp.server.web.dto.UserResponse;

public interface UserQueryService {

    UserResponse.Information getUserInformation(Long userId, String imageUrl);
}
