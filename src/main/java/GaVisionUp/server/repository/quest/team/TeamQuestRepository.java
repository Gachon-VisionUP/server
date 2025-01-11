package GaVisionUp.server.repository.quest.team;

import GaVisionUp.server.entity.quest.TeamQuest;

import java.util.List;

public interface TeamQuestRepository {
    List<TeamQuest> findByUserAndMonth(Long userId, int year, int month);
    TeamQuest save(TeamQuest quest);
}
