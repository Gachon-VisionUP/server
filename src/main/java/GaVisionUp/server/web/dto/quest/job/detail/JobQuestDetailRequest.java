package GaVisionUp.server.web.dto.quest.job.detail;

import GaVisionUp.server.entity.enums.Cycle;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class JobQuestDetailRequest {
    private String department;
    private int part;
    private Cycle cycle;
    private int month;
    private Integer week;
    private double sales;
    private double laborCost;
    private LocalDate recordedDate;
}
