package GaVisionUp.server.service.user;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.global.exception.RestApiException;
import GaVisionUp.server.global.exception.code.status.GlobalErrorStatus;
import GaVisionUp.server.repository.user.UserRepository;
import GaVisionUp.server.web.dto.user.UserRequest;
import GaVisionUp.server.web.dto.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandServiceImpl implements UserCommandService{

    private final UserRepository userRepository;


    @Override
    public UserResponse.UpdateInformation updateInformation(Long userId, UserRequest.Update request) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new RestApiException(GlobalErrorStatus._USER_NOT_EXIST));

        String changedImageUrl = user.getProfileImageUrl();

        if (request.getProfileImageUrl() != null) {
            changedImageUrl = request.getProfileImageUrl();
        }

        user.update(request.getChangedPW(), changedImageUrl);

        userRepository.save(user);

        return UserResponse.UpdateInformation.builder()
                .userId(user.getId())
                .build();
    }

    // ✅ Expo 푸쉬 토큰 업데이트 기능 추가
    @Override
    public void updatePushToken(Long userId, String pushToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._USER_NOT_EXIST));

        user.updatePushToken(pushToken);
        userRepository.save(user);
    }
}