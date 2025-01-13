package GaVisionUp.server.web.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PostRequest {

    @Schema(description = "게시글 생성 DTO")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddPost {

        @Schema(description = "제목")
        @NotNull(message = "제목을 입력해주세요.")
        @Size(max = 20, message = "제목은 20자 이내로 입력해주세요.")
        private String title;

        @Schema(description = "내용")
        @Size(max = 300, message = "내용은 300자 이내로 입력해주세요.")
        private String body;
    }
}