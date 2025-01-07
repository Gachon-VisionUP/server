package GaVisionUp.server.entity;

import jakarta.persistence.Table;
import lombok.Data;

import javax.management.relation.Role;
import java.time.LocalDate;

@Data
public class Users {
    private String id; // 사번
    private String name;
    private LocalDate joinDate; // 입사일 (YYYY-MM-DD 형식)
    private int joinNumber; // 입사번호 (2자리 숫자)
    private int jobGroup; // 직무 그룹
    private int level; // 레벨
    private String loginId; // 아이디
    private String password; // 패스워드
    private String changedPW; // 변경된 패스워드
    private int totalExp; // 총 경험치
    private String information; // 캐릭터 정보 (JSON 등 큰 데이터 저장 가능)
    private Role role;
}
