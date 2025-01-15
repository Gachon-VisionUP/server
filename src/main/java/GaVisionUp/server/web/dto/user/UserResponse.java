package GaVisionUp.server.web.dto.user;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.JobGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Getter
    @Setter
    @Builder
    public static class UserInfoDetail{
        private Department department;
        private int part;
        private String employeeId;
        private String userName;
        private LocalDate joinDate;
        private JobGroup jobGroup;
        private String loginId;
        private String changedPW;
    }
}