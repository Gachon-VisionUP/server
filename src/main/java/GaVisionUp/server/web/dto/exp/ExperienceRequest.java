package GaVisionUp.server.web.dto.exp;

import GaVisionUp.server.entity.enums.ExpType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExperienceRequest {
    private Long userId;
    private ExpType expType;
    private int exp;
}