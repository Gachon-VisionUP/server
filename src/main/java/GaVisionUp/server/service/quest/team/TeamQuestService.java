package GaVisionUp.server.service.quest.team;

import GaVisionUp.server.entity.enums.TeamQuestGrade;
import GaVisionUp.server.entity.quest.TeamQuest;

import java.time.LocalDate;
import java.util.List;

public interface TeamQuestService {
    List<TeamQuest> getUserMonthlyQuests(Long userId, int year, int month);
    void recordTeamQuest(Long userId, int year, int month, LocalDate recordedDate);
}
