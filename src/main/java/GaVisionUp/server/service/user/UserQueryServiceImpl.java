package GaVisionUp.server.service.user;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.repository.user.UserRepository;
import GaVisionUp.server.web.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService{

    private final UserRepository userRepository;

    @Override
    public UserResponse.Information getUserInformation(Long userId, String imageUrl) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원입니다."));

        return UserResponse.Information.builder()
                .profileImageUrl(imageUrl)
                .name(user.getName())
                .employeeId(user.getEmployeeId())
                .department(user.getDepartment())
                .joinDate(user.getJoinDate())
                .level(user.getLevel())
                .build();
    }
}