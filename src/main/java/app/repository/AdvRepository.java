package app.repository;

import app.model.AdvUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvRepository extends JpaRepository<AdvUser, Integer> {
    List<AdvUser> findAllByAdminChatIdOwner(Long chatId);

    List<AdvUser> findAllByUserName(String userName);
}
