package GaVisionUp.server.web.dto.quest.entire;

import GaVisionUp.server.entity.quest.EntireProject;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class EntireProjectResponse {
    private final Long id;
    private final Long userId;
    private final String userName;
    private final String projectName;
    private final int grantedExp;
    private final String note;
    private final LocalDate assignedDate;

    public EntireProjectResponse(EntireProject entireProject) {
        this.id = entireProject.getId();
        this.userId = entireProject.getUser().getId();
        this.userName = entireProject.getUser().getName();
        this.projectName = entireProject.getProjectName();
        this.grantedExp = entireProject.getGrantedExp();
        this.note = entireProject.getNote();
        this.assignedDate = entireProject.getAssignedDate();
    }
}
