package GaVisionUp.server.web.dto.exp;

import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.exp.Experience;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ExperienceResponse {
    private final ExpType expType;
    private final int exp;
    private final LocalDate obtainedDate;

    public ExperienceResponse(Experience experience) {
        this.expType = experience.getExpType();
        this.exp = experience.getExp();
        this.obtainedDate = experience.getObtainedDate();
    }
}
