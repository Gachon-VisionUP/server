package GaVisionUp.server.repository.post;

import GaVisionUp.server.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>/*, PostCustomRepository*/ {

}
