package app.handle;

import app.model.VipUser;
import app.service.VipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VipUserHandle {
    @Autowired
    private VipService vipService;

    public boolean addNewVipUser(Long ownerChatId, String text) {
        if (text.split("\\s+").length >= 2) {
            for (String s : text.split("\\s+")) {
                if (s.trim().replace(" ", "").contains("@")) {
                    VipUser vip  = new VipUser();

                    vip.setUserName(s.trim().replace(" ", ""));
                    vip.setOwnerChatId(ownerChatId);

                    vipService.save(vip);
                }
            }
            return true;
        }
        if (text.contains("@")) {

            VipUser vip  = new VipUser();

            vip.setUserName(text.trim().replace(" ", ""));
            vip.setOwnerChatId(ownerChatId);

            vipService.save(vip);
            return true;
        }
       return false;
    }
}
