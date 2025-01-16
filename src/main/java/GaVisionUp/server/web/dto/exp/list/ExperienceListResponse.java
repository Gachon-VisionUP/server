package GaVisionUp.server.web.dto.exp.list;

import GaVisionUp.server.web.dto.exp.ExperienceResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class ExperienceListResponse {
    private final int year;
    private final int totalCount;
    private final List<ExperienceResponse> allExperiences;

    public ExperienceListResponse(int year, int totalCount, List<ExperienceResponse> allExperiences) {
        this.year = year;
        this.totalCount = totalCount;
        this.allExperiences = allExperiences;
    }
}
