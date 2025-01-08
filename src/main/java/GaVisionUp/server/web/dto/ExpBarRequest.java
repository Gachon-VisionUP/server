package GaVisionUp.server.web.dto;

import GaVisionUp.server.entity.enums.Department;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExpBarRequest {
    private Long userId;
    private Department department;  // ✅ Enum 대신 String으로 받음
    private String name;
    private String level;

}
