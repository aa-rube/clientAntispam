package app.bot.enviroment.messages;

import app.bot.enviroment.keyboards.OwnersGroupsKeyboard;
import app.bot.enviroment.keyboards.StartKeyBord;
import app.model.GroupToListen;
import app.service.GroupListenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Service
public class OwnersGroupsMessage {
    @Autowired
    private StartKeyBord startKeyBord;
    @Autowired
    private OwnersGroupsKeyboard ownersGroupsKeyboard;
    @Autowired
    private GroupListenService groupListenService;
    private SendMessage getSendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        msg.setReplyMarkup(markup);
        msg.enableHtml(true);
        msg.setParseMode(ParseMode.HTML);
        return msg;
    }
    public SendMessage getEnterNewGroupUserName(StringBuilder builder,Long chatId) {
        builder.setLength(0);
        builder.append("Введите @username группы. Обязательно с \"@\"\n\n")
                .append("Группы можно ввожить списком через проблел или списком, где каждая группа с новой строки.");
        return getSendMessage(chatId, builder.toString(),ownersGroupsKeyboard.getBackToMyGroupsMainMenu());
    }

    public SendMessage getMsgAboutSaveNewGroup(StringBuilder builder,Long chatId, boolean isSave) {
        if (isSave) {
            return getSendMessage(chatId, "Данные сохранены успешно!\nМожно ввести еще группу или список.",
                    ownersGroupsKeyboard.getBackToMyGroupsMainMenu());
        }
        return getSendMessage(chatId, "Что-то пошло не так. Повторите попытку.",
                ownersGroupsKeyboard.getBackToMyGroupsMainMenu());
    }

    public SendMessage getMyGroupList(StringBuilder builder,Long chatId) {
        builder.setLength(0);
        builder.append("Список групп, в которых бот может работать:\n\n");
        for (GroupToListen group: groupListenService.findAllByOwnerChatId(chatId)) {
            builder.append(group.getGroupUserName()).append("\n")
                    .append("/deleteMyGroup_").append(group.getId()).append("\n\n");
        }
        return getSendMessage(chatId, builder.toString(), ownersGroupsKeyboard.getBackToMyGroupsMainMenu());
    }
}