package app.repository;

import app.model.GroupToListen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupToListenRepository extends JpaRepository<GroupToListen, Integer> {
    List<GroupToListen> findAllByOwnerChatId(Long chatId);
}
