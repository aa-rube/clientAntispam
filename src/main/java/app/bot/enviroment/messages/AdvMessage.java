package app.bot.enviroment.messages;

import app.bot.enviroment.keyboards.AdvertisersKeyboard;
import app.model.AdvUser;
import app.model.GroupToListen;
import app.service.AdvService;
import app.service.GroupListenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Service
public class AdvMessage {
    @Autowired
    private AdvService advService;
    @Autowired
    private AdvertisersKeyboard keyboard;
    @Autowired
    private GroupListenService groupService;
    private SendMessage getSendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        msg.setReplyMarkup(markup);
        msg.enableHtml(true);
        msg.setParseMode(ParseMode.HTML);
        return msg;
    }

    public SendMessage getAdvertisersStart(StringBuilder builder,Long chatId) {
        builder.setLength(0);
        builder.append("Тут можно настроить:\n-список партнеров\n-колличество публикаций\n-дату окончания договоренностей");
        return getSendMessage(chatId, builder.toString(), keyboard.getMainMenu());
    }

    public SendMessage addAdvertiser(StringBuilder builder,Long chatId) {
        builder.setLength(0);
        builder.append("Введите @username партнера");
        return getSendMessage(chatId, builder.toString(), keyboard.getBackToMain());
    }

    public SendMessage somethingWentWrong(StringBuilder builder,Long chatId) {
        builder.setLength(0);
        builder.append("что-то пошло не так. Повторите попытку.");
        return getSendMessage(chatId, builder.toString(), keyboard.getBackToMain());
    }

    public SendMessage getGroupsKeyList(StringBuilder builder,Long chatId, String userName, int i) {
        builder.setLength(0);
        builder.append("Выберите группу, где пользователь: ").append(userName).append(" может размещать посты");
        List<GroupToListen> groupsList = groupService.findAllByOwnerChatId(chatId);
        return getSendMessage(chatId, builder.toString(), keyboard.groupsKeyboards(groupsList, i));
    }

    public SendMessage addCountPostKeys(StringBuilder builder,Long chatId, AdvUser advUser) {
        builder.setLength(0);

        builder.append("Выберите колличество постов:\n")
                .append("Пользователь ").append(advUser.getUserName()).append("\n")
                .append("Группа:").append(advUser.getPermissionToGroup());

        return getSendMessage(chatId, builder.toString(), keyboard.getPostCountKeyboard());
    }

    public SendMessage dataSaved(StringBuilder builder,Long chatId, AdvUser user) {
        builder.setLength(0);
        builder.append("Пользователь: ").append(user.getUserName()).append("\n")
                .append("Может отправить ").append(user.getPostCount()).append(" постов ")
                .append("в группу: ").append(user.getPermissionToGroup()).append("\n")
                .append("до: ").append(user.getEndPermission() == null ? "период использования не начался\n" :
                        user.getEndPermission().toString().substring(0, 16)).append("\n")
                .append("Всего дней:").append(user.getDaysOfPermission()).append("\n\n")
                .append("Можно ввести @username следующего пользователя.");

        return getSendMessage(chatId, builder.toString(), keyboard.getBackToMain());
    }

    public SendMessage getListUsersPermission(StringBuilder builder,Long chatId) {
        builder.setLength(0);
        builder.append("СПИСОК РАЗРЕШЕНИЙ ПАРТНЕРАМ - КАЖДЫЙ ПАРТНЕР БУДЕТ В СПИСКЕ СТОЛЬКО РАЗ, ")
                .append("ВО СКОЛЬКО ГРУПП ЕСТЬ РАЗРЕШЕНИЕ ДЛЯ ПУБЛИКАЦИЙ\n\n");

        for (AdvUser user : advService.getAllUsersByOwnerChatId(chatId)) {
            builder.append("Партнер: ").append(user.getUserName()).append("\n")
                    .append("Группа: ").append(user.getPermissionToGroup()).append("\n")
                    .append("Постов осталось: ").append(user.getPostCount()).append("\n")

                    .append("до: ").append(user.getEndPermission() == null ?
                            "период использования не начался\n" :
                            user.getEndPermission().toString().substring(0, 16)).append("\n")

                    .append("Дней осталось: ").append(user.getDaysOfPermission()).append("\n")
                    .append("/deletePartner_").append(user.getId()).append("\n")
                    .append("/addOnePost_").append(user.getId()).append("\n")
                    .append("/removeOnePost_").append(user.getId()).append("\n\n");
        }

        return getSendMessage(chatId, builder.toString(), keyboard.getBackToMain());
    }

}