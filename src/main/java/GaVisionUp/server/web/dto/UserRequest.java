package GaVisionUp.server.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
