package app.bot.enviroment.messages;

import app.bot.enviroment.keyboards.AdminKeyboard;
import app.bot.enviroment.keyboards.UserKeyboard;
import app.model.UserClient;
import app.model.VipUser;
import app.service.UserClientService;
import app.service.VipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Optional;

@Service
public class AdminMessage {
    @Autowired
    private UserClientService userClientService;
    @Autowired
    private UserKeyboard keyboard;
    @Autowired
    private AdminKeyboard adminKeyboard;
    @Autowired
    private VipService vipService;

    private SendMessage getSendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        msg.setReplyMarkup(markup);
        msg.enableHtml(true);
        msg.setParseMode(ParseMode.HTML);
        return msg;
    }

    public SendMessage vipUsersMainMenu(StringBuilder builder, Long chatId) {
        builder.setLength(0);
        builder.append("Меню для управления VIP-пользователей. Сообшения пользователей из этого списка никогда не обрабатываюся ботом: не удаляются и не считаются посты и время начала подписки, если вы добавите их в пртнеры");
        return getSendMessage(chatId, builder.toString(), adminKeyboard.getOptionsForVipUsers());
    }

    public SendMessage addVipUsers(StringBuilder builder, String adding, Long chatId) {
        builder.setLength(0);
        builder.append(adding)
                .append("Введи @username нового пользователя для \uD83D\uDC51VIP списка.\n")
                .append("Добавить можно списком через пробел или переносом на новую строку");
        return getSendMessage(chatId, builder.toString(), adminKeyboard.getBackToVipOption());
    }

    public SendMessage getVipListUser(StringBuilder builder, Long chatId) {
        builder.setLength(0);
        builder.append("Список пользователей, сообщения которых никогда не удаляются:\n");

        for (VipUser user : vipService.findAllByOwnerChatId(chatId)) {
            builder.append(user.getUserName()).append("\n")
                    .append("/deleteVip_").append(user.getId()).append("\n\n");
        }
        return getSendMessage(chatId, builder.toString(), adminKeyboard.getBackToVipOption());
    }

}
