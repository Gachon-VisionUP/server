package GaVisionUp.server.entity.quest;

import GaVisionUp.server.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "entire_project")
public class EntireProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user; // ✅ 프로젝트 참여자

    @Column(nullable = false)
    private String projectName; // ✅ 전사 프로젝트명

    @Column(nullable = false)
    private int grantedExp; // ✅ 부여된 경험치

    @Column(nullable = true)
    private String note; // ✅ 비고 (추가 설명, 기본적으로 빈 값)

    @Column(nullable = false)
    private LocalDate assignedDate; // ✅ 프로젝트 참여 날짜

    public static EntireProject create(User user, String projectName, int grantedExp, String note, LocalDate assignedDate) {
        return EntireProject.builder()
                .user(user)
                .projectName(projectName)
                .grantedExp(grantedExp)
                .note(note)
                .assignedDate(assignedDate)
                .build();
    }
}
