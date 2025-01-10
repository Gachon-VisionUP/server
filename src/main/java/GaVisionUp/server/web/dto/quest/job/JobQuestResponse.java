package GaVisionUp.server.web.dto.quest.job;

import GaVisionUp.server.entity.quest.job.JobQuest;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class JobQuestResponse {
    private final String department;
    private final int part;
    private final String cycle;
    private final int round;
    private final double productivity;
    private final int grantedExp;
    private final LocalDate grantedDate;

    public JobQuestResponse(JobQuest jobQuest) {
        this.department = jobQuest.getDepartment().name();
        this.part = jobQuest.getPart();
        this.cycle = jobQuest.getCycle().name();
        this.round = jobQuest.getRound();
        this.productivity = jobQuest.getProductivity();
        this.grantedExp = jobQuest.getGrantedExp();
        this.grantedDate = jobQuest.getGrantedDate();
    }
}