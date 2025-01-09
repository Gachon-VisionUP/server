package GaVisionUp.server.web.dto;

import GaVisionUp.server.entity.enums.Department;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ExpBarRequest {
    private Long userId;  // ✅ User ID만 필요함
}