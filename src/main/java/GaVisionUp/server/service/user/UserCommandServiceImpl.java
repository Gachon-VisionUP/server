package GaVisionUp.server.service.user;

import GaVisionUp.server.entity.Level;
import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Role;
import GaVisionUp.server.global.exception.RestApiException;
import GaVisionUp.server.global.exception.code.status.GlobalErrorStatus;
import GaVisionUp.server.repository.level.LevelRepository;
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
    private final LevelRepository levelRepository;

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

    @Override
    public UserResponse.Create userCreate(Long userId, UserRequest.Create request) {

        if (!checkAdmin(userId)) {
            throw new RestApiException(GlobalErrorStatus._ONLY_ADMIN);
        }

        Level level = levelRepository.findByJobGroup(request.getJobGroup()).get(0);

        User newUser = User.builder()
                .employeeId(request.getEmployeeId())
                .name(request.getName())
                .joinDate(request.getJoinDate())
                .department(request.getDepartment())
                .part(request.getPart())
                .level(level)
                .loginId(request.getLoginId())
                .password("1111")
                .changedPW(request.getPassword())
                .role(Role.USER)
                .build();

        userRepository.save(newUser);

        return UserResponse.Create.builder()
                .userId(newUser.getId())
                .build();
    }

    @Override
    public UserResponse.UpdateInformation updateUserInfo(Long userId, Long targetId, UserRequest.UpdateUserInfo request) {

        if (!checkAdmin(userId)) {
            throw new RestApiException(GlobalErrorStatus._ONLY_ADMIN);
        }

        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._USER_NOT_EXIST));

        if (target.getLevel().getJobGroup() != request.getJobGroup()) {
            target.setLevel(levelRepository.findByJobGroup(request.getJobGroup()).get(0));
        }

        if (request.getChangedPW() != null || !request.getChangedPW().isEmpty() || !target.getChangedPW().equals(request.getChangedPW())) {
            target.updatePassword(request.getChangedPW());
        }

        target.updateInfo(request);

        userRepository.save(target);

        return UserResponse.UpdateInformation.builder()
                .userId(target.getId())
                .build();
    }

    private boolean checkAdmin(Long userId) {
        User admin = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._USER_NOT_EXIST));

        return admin.getRole() == Role.ADMIN;
    }
}