package GaVisionUp.server.entity;

import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String employeeId; // 사번

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate joinDate; // 입사일 (YYYY-MM-DD 형식)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department; // 소속

    @Column(nullable = false)
    private int part; // 직무 그룹

    @Column(nullable = false)
    private int level; // 레벨

    @Column(unique = true, nullable = false)
    private String loginId; // 아이디

    @Column(nullable = false)
    private String password; // 패스워드

    private String changedPW; // 변경된 패스워드

    private int totalExp; // 총 경험치

    @Lob
    private String information; // 캐릭터 정보 (JSON 등 큰 데이터 저장 가능)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String profileImageUrl;
}