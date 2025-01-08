package GaVisionUp.server.web.dto;

import GaVisionUp.server.entity.enums.ExpType;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ExperienceResponse {
    private final Long id;
    private final Long userId;
    private final ExpType expType;
    private final int exp;
    private final LocalDate obtainedDate;

    public ExperienceResponse(Long id, Long userId, ExpType expType, int exp, LocalDate obtainedDate) {
        this.id = id;
        this.userId = userId;
        this.expType = expType;
        this.exp = exp;
        this.obtainedDate = obtainedDate;
    }
}
