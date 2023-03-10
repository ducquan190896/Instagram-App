package instagram.com.backend.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import instagram.com.backend.Entity.PostNotification;
import instagram.com.backend.Entity.Users;

@Repository
public interface PostNotificationRepos extends JpaRepository<PostNotification, Long> {
    List<PostNotification> findByReceiver(Users receiver);
}
