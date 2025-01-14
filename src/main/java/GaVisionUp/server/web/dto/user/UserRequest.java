package GaVisionUp.server.web.dto.user;

import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.JobGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class UserRequest {

    @Schema(description = "로그인 DTO")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Login {

        @Schema(description = "로그인 id")
        @NotNull
        @Size(max = 20, message = "아이디는 20자 이내로 입력해주세요.")
        private String loginId;

        @Schema(description = "로그인 pw")
        @Size(max = 10, message = "비밀번호는 10자 이내로 입력해주세요.")
        private String password;
    }

    @Schema(description = "정보 수정 DTO")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Update {

        @Schema(description = "변경할 캐릭터")
        private String profileImageUrl;

        @Schema(description = "변경할 비밀번호")
        @Size(max = 30, message = "비밀번호는 10자 이내로 입력해주세요.")
        private String changedPW;
    }

    @Schema(description = "계정 생성 DTO")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Create {

        @Schema(description = "부서")
        @NotNull
        private Department department;

        @Schema(description = "소속")
        @NotNull
        private int part;

        @Schema(description = "사번")
        @NotNull
        private String employeeId;

        @Schema(description = "이름")
        @NotNull
        private String name;

        @Schema(description = "입사일")
        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate joinDate;

        @Schema(description = "직군")
        @NotNull
        private JobGroup jobGroup;

        @Schema(description = "아이디")
        @Size(max = 20, message = "아이디는 20자 이내로 입력해주세요.")
        @NotNull
        private String loginId;

        @Schema(description = "비밀번호")
        @Size(max = 10, message = "비밀번호는 10자 이내로 입력해주세요.")
        private String password;
    }

    @Schema(description = "구성원 정보 수정 DTO")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateUserInfo {

        @Schema(description = "부서")
        @NotNull
        private Department department;

        @Schema(description = "소속")
        @NotNull
        private int part;

        @Schema(description = "사번")
        @NotNull
        private String employeeId;

        @Schema(description = "이름")
        @NotNull
        private String name;

        @Schema(description = "입사일")
        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate joinDate;

        @Schema(description = "직군")
        @NotNull
        private JobGroup jobGroup;

        @Schema(description = "아이디")
        @Size(max = 20, message = "아이디는 20자 이내로 입력해주세요.")
        @NotNull
        private String loginId;

        @Schema(description = "비밀번호")
        @Size(max = 10, message = "비밀번호는 10자 이내로 입력해주세요.")
        private String changedPW;
    }
}