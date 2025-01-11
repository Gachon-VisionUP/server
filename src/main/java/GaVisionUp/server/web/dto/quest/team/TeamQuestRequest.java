package GaVisionUp.server.web.dto.quest.team;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class TeamQuestRequest {
    private Long userId;
    private int year; // ✅ 연도 정보 추가
    private int month; // ✅ 퀘스트 달성 월 (1~12)
    private LocalDate recordedDate; // ✅ 퀘스트 기록 날짜
}
