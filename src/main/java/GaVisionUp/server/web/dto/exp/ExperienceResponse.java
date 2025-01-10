package GaVisionUp.server.web.dto.exp;

import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.exp.Experience;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ExperienceResponse {
    private final Long id;
    private final Long userId;
    private final ExpType expType;
    private final int exp;
    private final LocalDate obtainedDate;

    public ExperienceResponse(Experience experience) {
        this.id = experience.getId();
        this.userId = experience.getUser().getId();  // ✅ Lazy-Loaded Proxy 방지
        this.expType = experience.getExpType();
        this.exp = experience.getExp();
        this.obtainedDate = experience.getObtainedDate();
    }
}
