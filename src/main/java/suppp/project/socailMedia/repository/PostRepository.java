package suppp.project.socailMedia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import suppp.project.socailMedia.dto.Post;
import suppp.project.socailMedia.dto.User;

public interface PostRepository extends JpaRepository<Post, Integer>{

	List<Post> findByUser(User user);

}
