package suppp.project.socailMedia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import suppp.project.socailMedia.dto.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	boolean existsByEmail(String email);

	boolean existsByMobile(long mobile);

	boolean existsByUserName(String userName);

	User findByUserName(String userName);

	List<User> findByVerifiedTrue();
}
