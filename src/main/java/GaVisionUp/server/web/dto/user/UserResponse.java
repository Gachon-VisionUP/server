package GaVisionUp.server.web.dto.user;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Department;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

public class UserResponse {

    @Getter
    @Setter
    @Builder
    public static class Login{
        User user;
    }

    @Getter
    @Setter
    @Builder
    public static class Information{
        String profileImageUrl;
        String name;
        String employeeId;
        Department department;
        LocalDate joinDate;
        String level;
    }

    @Getter
    @Setter
    @Builder
    public static class UpdateInformation{
        Long userId;
    }

    @Getter
    @Setter
    @Builder
    public static class Create{
        Long userId;
    }

    @Getter
    @Setter
    @Builder
    public static class UserInfoList{
        List<UserInfo> userInfoList;
    }

    @Getter
    @Setter
    @Builder
    public static class UserInfo{
        Long userId;
        Department department;
        int part;
        String employeeId;
        String userName;
    }
}