package GaVisionUp.server.service.user;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.repository.user.UserRepository;
import GaVisionUp.server.web.dto.UserRequest;
import GaVisionUp.server.web.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
                orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원입니다."));

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
}