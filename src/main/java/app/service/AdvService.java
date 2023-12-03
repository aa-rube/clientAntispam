package app.service;

import app.model.AdvUser;
import app.repository.AdvRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdvService {
    @Autowired
    private AdvRepository repository;

    public boolean save(AdvUser user) {
        try {
            repository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<AdvUser> getAllUsers() {
        return repository.findAll();
    }

    public List<AdvUser> getAllUsersByOwnerChatId(Long chatId) {
        return  repository.findAllByAdminChatIdOwner(chatId);
    }

    public boolean deleteByUserId(Long chatId, int id) {
        if (userChatIdEqualsChatIdOwner(chatId, id)) {
            try {
                repository.deleteById(id);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public AdvUser getUserById(Integer id) {
        return repository.findById(id).get();
    }

    public List<AdvUser> getAllUsersByUserName(String userName) {
        return repository.findAllByUserName(userName);
    }

    private boolean userChatIdEqualsChatIdOwner(Long chatId, int id) {
        Optional<AdvUser> advOpt = repository.findById(id);

        return advOpt.map(advUser -> advUser.getAdminChatIdOwner().equals(chatId)).orElse(false);
    }

    public boolean autoDeleteByUserId(Integer id) {
        try {
            repository.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
