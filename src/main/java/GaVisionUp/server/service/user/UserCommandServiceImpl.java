package GaVisionUp.server.service.user;

import GaVisionUp.server.entity.Level;
import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Role;
import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.global.exception.RestApiException;
import GaVisionUp.server.global.exception.code.status.GlobalErrorStatus;
import GaVisionUp.server.repository.level.LevelRepository;
import GaVisionUp.server.repository.user.UserRepository;
import GaVisionUp.server.service.exp.expbar.ExpBarService;
import GaVisionUp.server.service.google.GoogleUserService;
import GaVisionUp.server.web.dto.user.UserRequest;
import GaVisionUp.server.web.dto.user.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserCommandServiceImpl implements UserCommandService{

    private final UserRepository userRepository;
    private final LevelRepository levelRepository;
    private final GoogleUserService googleUserService;
    private final ExpBarService expBarService;

    @Override
    public UserResponse.UpdateInformation changePassword(Long userId, UserRequest.ChangePassword request) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new RestApiException(GlobalErrorStatus._USER_NOT_EXIST));

        if (!request.getChangedPW().equals(request.getCheckPW())) {
            throw new RestApiException(GlobalErrorStatus._NOT_EQUAL_PASSWORD);
        }

        String userPassword = user.getChangedPW() == null ? user.getPassword() : user.getChangedPW();

        if (userPassword.equals(request.getChangedPW())) {
                throw new RestApiException(GlobalErrorStatus._SAME_PASSWORD);
        }

        if (user.getPassword().equals(request.getChangedPW()) || user.getChangedPW().equals(request.getChangedPW())) {
            throw new RestApiException(GlobalErrorStatus._SAME_PASSWORD);
        }

        user.updatePassword(request.getChangedPW());
        userRepository.save(user);

        googleUserService.syncDatabaseToGoogleSheet();

        return UserResponse.UpdateInformation.builder()
                .userId(user.getId())
                .build();
    }

    @Override
    public UserResponse.UpdateInformation changeImage(Long userId, String url) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new RestApiException(GlobalErrorStatus._USER_NOT_EXIST));

        String changedImageUrl = user.getProfileImageUrl();

        if (url != null) {
            changedImageUrl = url;
        }

        user.updateImageUrl(changedImageUrl);

        userRepository.save(user);

        googleUserService.syncDatabaseToGoogleSheet();

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

        if (checkAdmin(userId)) {
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
                .profileImageUrl("man-01")
                .build();

        userRepository.save(newUser);

        expBarService.getOrCreateExpBarByUserId(newUser.getId());

        googleUserService.syncDatabaseToGoogleSheet();

        newUser.updatePushToken("ExponentPushToken"+newUser.getId());

        return UserResponse.Create.builder()
                .userId(newUser.getId())
                .build();
    }

    @Override
    public UserResponse.UpdateInformation updateUserInfo(Long userId, Long targetId, UserRequest.UpdateUserInfo request) {

        if (checkAdmin(userId)) {
            throw new RestApiException(GlobalErrorStatus._ONLY_ADMIN);
        }

        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._USER_NOT_EXIST));

        if (target.getLevel().getJobGroup() != request.getJobGroup()) {
            target.setLevel(levelRepository.findByJobGroup(request.getJobGroup()).get(0));
            target.setLevel(levelRepository.findLevelByExp(request.getJobGroup(), target.getTotalExp())
                    .orElseThrow(() -> new IllegalArgumentException("해당 경험치에 맞는 레벨을 찾을 수 없습니다.")));
        }

        if (request.getChangedPW() != null) {
            String userPassword = target.getChangedPW() == null ? target.getPassword() : target.getChangedPW();

            if (userPassword.equals(request.getChangedPW())) {
                throw new RestApiException(GlobalErrorStatus._SAME_PASSWORD);
            }
            target.updatePassword(request.getChangedPW());
        }

        target.updateInfo(request);

        userRepository.save(target);

        googleUserService.syncDatabaseToGoogleSheet();

        return UserResponse.UpdateInformation.builder()
                .userId(target.getId())
                .build();
    }

    private boolean checkAdmin(Long userId) {
        User admin = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._USER_NOT_EXIST));

        return admin.getRole() != Role.ADMIN;
    }
}