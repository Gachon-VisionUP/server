package GaVisionUp.server.service.user;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.repository.user.UserRepository;
import GaVisionUp.server.web.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService{

    private final UserRepository userRepository;

    @Value("${server.url}") // 서버 URL (예: http://localhost:8080)
    private String serverUrl;

    @Override
    public UserResponse.Information getUserInformation(Long userId) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원입니다."));

        String profileImageUrl = serverUrl + "/images/" + user.getProfileImageUrl() + ".png";

        return UserResponse.Information.builder()
                .profileImageUrl(profileImageUrl)
                .name(user.getName())
                .employeeId(user.getEmployeeId())
                .department(user.getDepartment())
                .joinDate(user.getJoinDate())
                .level(user.getLevel())
                .build();
    }
}