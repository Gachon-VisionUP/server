package GaVisionUp.server.service.quest.job.detail;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.quest.job.JobQuestDetail;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JobQuestDetailService {
    List<JobQuestDetail> getJobQuestDetails(String department, int part, Cycle cycle, int month, Integer week);
    List<JobQuestDetail> getAllJobQuestDetails(String department, int part, String cycle);
    JobQuestDetail saveJobQuestDetail(String department, int part, Cycle cycle, int month, Integer week, double sales, double laborCost, LocalDate recordedDate);
}
