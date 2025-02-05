package GaVisionUp.server.service.user;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Role;
import GaVisionUp.server.global.exception.RestApiException;
import GaVisionUp.server.global.exception.code.status.GlobalErrorStatus;
import GaVisionUp.server.repository.user.UserRepository;
import GaVisionUp.server.web.dto.user.UserRequest;
import GaVisionUp.server.web.dto.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService{

    private final UserRepository userRepository;

    @Value("${server.url}") // 서버 URL (예: http://localhost:8080)
    private String serverUrl;


    /**
     *  로그인 기능
     *  화면에서 LoginRequest(loginId, password)을 입력받아 loginId와 password가 일치하면 User return
     *  loginId가 존재하지 않거나 password가 일치하지 않으면 예외 던짐
     */
    public User login(UserRequest.Login request) {
        Optional<User> optionalUser = userRepository.findByLoginId(request.getLoginId());

        // loginId와 일치하는 User가 없으면 null return
        if(optionalUser.isEmpty()) {
            throw new RestApiException(GlobalErrorStatus._ID_WRONG);
        }

        User user = optionalUser.get();

        // 찾아온 User의 password와 입력된 password가 다르면 null return
        String userPassword = user.getChangedPW();
        if (userPassword == null || userPassword.isEmpty()) {
            userPassword = user.getPassword();
        }

        if(!userPassword.equals(request.getPassword())) {
            throw new RestApiException(GlobalErrorStatus._PW_WRONG);
        }

        return user;
    }

    @Override
    public UserResponse.Information getUserInformation(Long userId) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new RestApiException(GlobalErrorStatus._USER_NOT_EXIST));

        String profileImageUrl = serverUrl + "/images/" + user.getProfileImageUrl() + ".png";

        return UserResponse.Information.builder()
                .profileImageUrl(profileImageUrl)
                .name(user.getName())
                .employeeId(user.getEmployeeId())
                .department(user.getDepartment())
                .joinDate(user.getJoinDate())
                .level(user.getLevel().getLevelName())
                .build();
    }

    // ✅ 유저 ID를 기반으로 사용자 정보 조회
    @Override
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    /*** 모든 사용자의 Expo Push Token 가져오기 */
    @Override
    public List<String> getAllExpoPushTokens() {
        return userRepository.findAll().stream()
                .map(User::getExpoPushToken)
                .filter(token -> token != null && !token.isEmpty())
                .toList();
    }

    @Override
    public UserResponse.UserInfoList getUserInfoList(Long userId, int page, int size) {

        if (checkAdmin(userId)) {
            throw new RestApiException(GlobalErrorStatus._ONLY_ADMIN);
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<User> userList = userRepository.findAllByOrderByIdDesc(pageable);

        List<UserResponse.UserInfo> userInfoList =
                userList.stream().map(user ->
                        UserResponse.UserInfo.builder()
                                .userId(user.getId())
                                .department(user.getDepartment())
                                .part(user.getPart())
                                .employeeId(user.getEmployeeId())
                                .userName(user.getName())
                                .build()).toList();

        return UserResponse.UserInfoList.builder().userInfoList(userInfoList).build();
    }

    @Override
    public UserResponse.UserInfoDetail getUserInfoDetail(Long userId, Long targetId) {

        if (checkAdmin(userId)) {
            throw new RestApiException(GlobalErrorStatus._ONLY_ADMIN);
        }

        User target = userRepository.findById(userId).
                orElseThrow(() -> new RestApiException(GlobalErrorStatus._USER_NOT_EXIST));

        String password = target.getPassword();
        if (target.getChangedPW() != null || !target.getChangedPW().isEmpty()) {
            password = target.getChangedPW();
        }

        return UserResponse.UserInfoDetail.builder()
                .department(target.getDepartment())
                .part(target.getPart())
                .employeeId(target.getEmployeeId())
                .userName(target.getName())
                .joinDate(target.getJoinDate())
                .jobGroup(target.getLevel().getJobGroup())
                .loginId(target.getLoginId())
                .changedPW(password)
                .build();
    }

    private boolean checkAdmin(Long userId) {
        User admin = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._USER_NOT_EXIST));

        return admin.getRole() != Role.ADMIN;
    }

    // 스프레드 시트 연동 테스트
    // ✅ 모든 유저 조회 (Google Sheets 동기화용)
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}