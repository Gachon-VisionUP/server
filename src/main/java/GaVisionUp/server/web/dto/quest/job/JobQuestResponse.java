package GaVisionUp.server.web.dto.quest.job;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.TeamQuestGrade;
import GaVisionUp.server.entity.quest.job.JobQuest;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class JobQuestResponse {
    private String department;
    private int part;
    private Cycle cycle;
    private int round;
    private int month;
    private Integer week;
    private double productivity;
    private TeamQuestGrade questGrade;
    private int grantedExp;
    private LocalDate grantedDate;

    public JobQuestResponse(JobQuest jobQuest) {
        this.department = jobQuest.getDepartment().name();
        this.part = jobQuest.getPart();
        this.cycle = jobQuest.getCycle();
        this.round = jobQuest.getRound();
        this.month = jobQuest.getMonth();
        this.week = jobQuest.getWeek();
        this.productivity = jobQuest.getProductivity();
        this.questGrade = jobQuest.getQuestGrade();
        this.grantedExp = jobQuest.getGrantedExp();
        this.grantedDate = jobQuest.getGrantedDate();
    }
}
