package GaVisionUp.server.web.dto.quest.job.detail;

import GaVisionUp.server.entity.quest.job.JobQuestDetail;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class JobQuestDetailResponse {
    private final String department;
    private final int part;
    private final String cycle;
    private final int round;
    private final double sales;
    private final double laborCost;
    private final LocalDate recordedDate;

    public JobQuestDetailResponse(JobQuestDetail jobQuestDetail) {
        this.department = jobQuestDetail.getDepartment().name();
        this.part = jobQuestDetail.getPart();
        this.cycle = jobQuestDetail.getCycle().name();
        this.round = jobQuestDetail.getRound();
        this.sales = jobQuestDetail.getSales();
        this.laborCost = jobQuestDetail.getLaborCost();
        this.recordedDate = jobQuestDetail.getRecordedDate();
    }
}
