package GaVisionUp.server.web.dto.exp.list;

import GaVisionUp.server.web.dto.exp.ExperienceResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class ExperienceListResponse {
    private final int year;
    private final List<ExperienceResponse> top3Experiences;

    public ExperienceListResponse(int year, List<ExperienceResponse> top3Experiences) {
        this.year = year;
        this.top3Experiences = top3Experiences;
    }
}
