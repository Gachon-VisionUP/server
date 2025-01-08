package GaVisionUp.server.repository.user;

import GaVisionUp.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginIdAndPassword(String loginId, String password);
    Optional<User> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);
}
