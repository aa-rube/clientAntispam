package app.repository;

import app.model.VipUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VipUserRepository extends JpaRepository<VipUser, Integer> {
    List<VipUser> findAllByOwnerChatId(Long chatId);
}
