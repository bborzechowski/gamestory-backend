package bb.orzechowski.gamestory.auth.User;

import bb.orzechowski.gamestory.model.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserApp, Long> {

    UserApp findByUsername(String username);

}
