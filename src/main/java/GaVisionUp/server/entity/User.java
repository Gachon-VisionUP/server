package GaVisionUp.server.entity;

import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.Role;
import GaVisionUp.server.web.dto.user.UserRequest;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;  // 레벨

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

    @Column(nullable = true)
    private String expoPushToken; // ✅ Expo 푸쉬 토큰 추가

    // ✅ 경험치 추가 메서드
    public void addExperience(int exp) {
        this.totalExp += exp;
    }

    // ✅ 레벨 업데이트
    public void setLevel(Level level) {
        this.level = level;
    }

    public void updatePassword(String password) {
        this.changedPW = password;
    }

    public void updateImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void updateInfo(UserRequest.UpdateUserInfo request) {
        this.department = request.getDepartment();
        this.part = request.getPart();
        this.employeeId = request.getEmployeeId();
        this.name = request.getName();
        this.joinDate = request.getJoinDate();
        this.loginId = request.getLoginId();
        this.changedPW = request.getChangedPW();
    }

    // ✅ 푸쉬 토큰 업데이트 메서드
    public void updatePushToken(String pushToken) {
        this.expoPushToken = pushToken;
    }

    // 스프레드 시트용
    /**
     * ✅ 기존 유저 업데이트
     */
    public void updateUser(String name, LocalDate joinDate, Department department, int part, Level level,
                           String loginId, String password, String changedPw, int totalExp, Role role) {
        this.name = name;
        this.joinDate = joinDate;
        this.department = department;
        this.part = part;
        this.level = level;
        this.loginId = loginId;
        this.password = password;
        this.changedPW = changedPw;
        this.totalExp = totalExp;
        this.role = role;
    }

    /**
     * ✅ 새 유저 생성
     */
    public static User create(String employeeId, String name, LocalDate joinDate, Department department, int part,
                              Level level, String loginId, String password, String changedPw, int totalExp, Role role) {
        return User.builder()
                .employeeId(employeeId)
                .name(name)
                .joinDate(joinDate)
                .department(department)
                .part(part)
                .level(level)
                .loginId(loginId)
                .password(password)
                .changedPW(null)
                .totalExp(totalExp)
                .information(null)
                .role(role)
                .profileImageUrl("https://example.com/profile.jpg\t")
                .expoPushToken(null)
                .build();
    }
}