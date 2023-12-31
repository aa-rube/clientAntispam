package app.handle;

import app.model.AdvUser;
import app.service.AdvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class AdvHandler {
    @Autowired
    private AdvService advService;

    public AdvUser setDays(String data, AdvUser advUser) {
        int count = Integer.parseInt(data.split("_")[1]);
        int days = Integer.parseInt(data.split("_")[2]);

        advUser.setPostCount(count);
        advUser.setDaysOfPermission(days);
        advUser.setEndPermission(null);
        if (advService.save(advUser)) return advUser;
        return null;
    }

    public AdvUser createNew(Update update, Long chatId) {
        AdvUser user = new AdvUser();
        user.setAdminUserNameOwner("@" + update.getCallbackQuery().getFrom().getUserName());
        user.setAdminChatIdOwner(chatId);
        user.setStarted(0);
        return user;
    }
}
