package app.service;

import app.model.UserClient;
import app.repository.UserClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.AccessType;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserClientService {
    @Autowired
    private UserClientRepository userClientRepository;

    public UserClient getNewClient(Long chatId, String userName) {
        UserClient client = null;

        if (findByChatId(chatId).isPresent()) {
            client = findByChatId(chatId).get();
            client.setUserClintName("@" + userName);
            return client;
        }

        client = new UserClient();
        client.setChatId(chatId);
        client.setUserClintName("@" + userName);
        client.setPaid(false);
        client.setCheckPhoto(false);
        client.setSubscriptionEnd(LocalDateTime.now());

        return client;
    }

    public boolean deleteById(Long clientChatId) {
        try {
            userClientRepository.deleteById(clientChatId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void save(UserClient user) {
        userClientRepository.save(user);
    }

    public Optional<UserClient> findByChatId (Long chatId) {
        return userClientRepository.findById(chatId);
    }

    public List<UserClient> findAll() {
        return userClientRepository.findAll();
    }
}
