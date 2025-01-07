package GaVisionUp.server.web.dto;

import GaVisionUp.server.entity.enums.Department;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class UserResponse {

    @Getter
    @Setter
    @Builder
    public static class Information{
        String profileImageUrl;
        String name;
        String employeeId;
        Department department;
        LocalDate joinDate;
        int level;
    }
}
