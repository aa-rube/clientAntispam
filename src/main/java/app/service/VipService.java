package app.service;

import app.model.GroupToListen;
import app.model.VipUser;
import app.repository.VipUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VipService {
    @Autowired
    private VipUserRepository vipUserRepository;

    public void save(VipUser vip) {
        vipUserRepository.save(vip);
    }

    public boolean delete(Long chatId, int id) {
        Optional<VipUser> vipOpt = vipUserRepository.findById(id);

        if (vipOpt.isPresent()) {
            if (vipOpt.get().getOwnerChatId().equals(chatId)) {
                try {
                    vipUserRepository.deleteById(id);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }

        return false;
    }

    public List<VipUser> findAllVipUsers() {
        return vipUserRepository.findAll();
    }

    public List<VipUser> findAllByOwnerChatId(Long chatId) {
        return vipUserRepository.findAllByOwnerChatId(chatId);
    }
}
