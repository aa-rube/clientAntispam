package app.service;

import app.model.GroupToListen;
import app.repository.GroupToListenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupListenService {
    @Autowired
    private GroupToListenRepository groupToListenRepository;

    public boolean save(Long ownerChatId, String text) {

        if (text.split("\\s+").length > 1) {

            for (String s : text.split("\\s+")) {
                GroupToListen group = new GroupToListen();

                if (s.trim().replace(" ", "").contains("@")) {
                    group.setOwnerChatId(ownerChatId);
                    group.setGroupUserName(s.trim().replace(" ", ""));
                    groupToListenRepository.save(group);
                }
            }
            return true;
        }

        if (text.contains("@")) {
            GroupToListen group = new GroupToListen();
            group.setOwnerChatId(ownerChatId);
            group.setGroupUserName(text.trim().replace(" ", ""));
            groupToListenRepository.save(group);
            return true;
        }
        return false;
    }

    public List<GroupToListen> findAll() {
        return groupToListenRepository.findAll();
    }

    public List<GroupToListen> findAllByOwnerChatId(Long chatId) {
        return groupToListenRepository.findAllByOwnerChatId(chatId);
    }

    public boolean delete(Long chatId, int id) {
        Optional<GroupToListen> groupOpt = groupToListenRepository.findById(id);

        if (groupOpt.isPresent()) {
            if (groupOpt.get().getOwnerChatId().equals(chatId)) {
                try {
                    groupToListenRepository.deleteById(id);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }

        return false;
    }
}
