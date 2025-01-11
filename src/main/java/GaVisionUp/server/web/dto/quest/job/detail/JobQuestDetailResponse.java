package GaVisionUp.server.web.dto.quest.job.detail;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.quest.job.JobQuestDetail;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class JobQuestDetailResponse {
    private String department;
    private int part;
    private Cycle cycle;
    private int month;
    private Integer week;
    private double sales;
    private double laborCost;
    private LocalDate recordedDate;

    public JobQuestDetailResponse(JobQuestDetail jobQuestDetail) {
        this.department = jobQuestDetail.getDepartment().name();
        this.part = jobQuestDetail.getPart();
        this.cycle = jobQuestDetail.getCycle();
        this.month = jobQuestDetail.getMonth();
        this.week = jobQuestDetail.getWeek();
        this.sales = jobQuestDetail.getSales();
        this.laborCost = jobQuestDetail.getLaborCost();
        this.recordedDate = jobQuestDetail.getRecordedDate();
    }
}
