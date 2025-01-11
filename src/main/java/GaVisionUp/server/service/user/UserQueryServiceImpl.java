package GaVisionUp.server.service.user;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.global.exception.RestApiException;
import GaVisionUp.server.global.exception.code.status.GlobalErrorStatus;
import GaVisionUp.server.repository.user.UserRepository;
import GaVisionUp.server.web.dto.user.UserRequest;
import GaVisionUp.server.web.dto.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}