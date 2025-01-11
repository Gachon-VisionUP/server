package GaVisionUp.server.web.dto.quest;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class EntireProjectRequest {
    private Long userId; // ✅ 프로젝트 참여자 ID
    private String projectName; // ✅ 전사 프로젝트명
    private int grantedExp; // ✅ 부여된 경험치
    private String note; // ✅ 비고 (추가 설명)
    private LocalDate assignedDate; // ✅ 프로젝트 참여 날짜
}
