package GaVisionUp.server.repository.user;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginIdAndPassword(String loginId, String password);
    Optional<User> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    List<User> findByDepartmentAndPart(Department department, int part);

    List<User> findAllByRoleEquals(Role role);

    Page<User> findAllByOrderByIdDesc(Pageable pageable);

    boolean existsByRole(Role role);
    Optional<User> findByEmployeeId(String employeeId);
    /**
     * ✅ 특정 사번 목록(Set)으로 유저 조회 (삭제할 유저 찾을 때 사용)
     */
    List<User> findByEmployeeIdIn(Set<String> employeeIds);
}
