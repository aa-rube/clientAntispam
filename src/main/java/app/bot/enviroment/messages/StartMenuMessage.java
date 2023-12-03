package app.bot.enviroment.messages;

import app.bot.enviroment.keyboards.StartKeyBord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Service
public class StartMenuMessage {
    @Autowired
    StartKeyBord startKeyBord;
    public SendMessage getSendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        msg.setReplyMarkup(markup);
        msg.enableHtml(true);
        msg.setParseMode(ParseMode.HTML);
        return msg;
    }

    public SendMessage getMainClientMenu(StringBuilder builder,Long chatId, boolean check) {
        builder.setLength(0);
        builder.append("Это АнтиСпам бот. Чем займемся?");
        return getSendMessage(chatId, builder.toString(), startKeyBord.mainOwnerKeyboard(check));
    }

    public SendMessage getMainAdminMenu(StringBuilder builder,Long chatId) {
        builder.setLength(0);
        builder.append("Это АнтиСпам бот. Чем займемся??");
        return getSendMessage(chatId, builder.toString(), startKeyBord.mainAdminKeyboard());
    }

    public SendMessage getStopWordMainMenu(StringBuilder builder,Long chatId) {
        builder.setLength(0);
        builder.append("Создай свой список стоп-слов");
        return getSendMessage(chatId, builder.toString(), startKeyBord.getStopWordMenu());
    }

    public SendMessage getAdminsMainMenu(StringBuilder builder,Long chatId) {
        builder.setLength(0);
        builder.append("Добавь или удали админа. Эта привелегия есть только у Владельца.");
        return getSendMessage(chatId,builder.toString(), startKeyBord.getAdmins());
    }

    public SendMessage getGroupsMainMenu(StringBuilder builder,Long chatId) {
        builder.setLength(0);
        builder.append("Что бы бот смог удалять удалять спам-сообщения в Ваших группах нужно:\n\n")
                .append("1. Добавить бота в групу и выдать разрешение на удаление и блокировку пользователей)\n")
                .append("2. Добавить @my_group группы в этот список\n");
        return getSendMessage(chatId, builder.toString(), startKeyBord.getMyGroupsMainMenu());
    }
}
