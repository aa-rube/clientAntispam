package app.bot.enviroment.messages;

import app.bot.enviroment.keyboards.GodKeyboard;
import app.bot.enviroment.keyboards.UserKeyboard;
import app.model.UserClient;
import app.service.UserClientService;
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
public class GodMessage {
    @Autowired
    private GodKeyboard keyboard;
    @Autowired
    private UserClientService userClientService;
    @Autowired
    private UserClientService clientService;
    @Autowired
    private UserKeyboard userKeyboard;

    private SendMessage getSendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        msg.setReplyMarkup(markup);
        msg.enableHtml(true);
        msg.setParseMode(ParseMode.HTML);
        return msg;
    }

    public SendMessage start(StringBuilder builder, Long chatId) {
        builder.setLength(0);
        builder.append("Привет! Это админка клиентского антиспам бота");
        return getSendMessage(chatId, builder.toString(), keyboard.getStart());
    }

    public SendMessage clientsList(StringBuilder builder, Long ownerChatId) {
        builder.setLength(0);
        builder.append("Cписок пользователей:\n");

        int i = 0;
        for (UserClient client : userClientService.findAll()) {
            ++i;
            builder.append(i).append(". ").append(client.getUserClintName()).append("\n");
            if (client.getIsPaid() == 1) {
                builder.append("Оплачено до: ")
                        .append(client.getSubscriptionEnd().toString().substring(0, 10));
            } else {
                builder.append("не оплачено❗");
            }
            builder.append("\n/deleteClient_").append(client.getChatId()).append("\n\n");
        }

        return getSendMessage(ownerChatId, builder.toString(), keyboard.getBack());
    }


    public SendMessage deleteClient(StringBuilder builder, Long ownerChatId, String text) {
        Long clientChatId = Long.valueOf(text.split("_")[1]);

        if (clientService.deleteById(clientChatId)) {
            builder.setLength(0);
            builder.append("Пользователь удален!");
        } else {
            builder.setLength(0);
            builder.append("Произошла ошибка! Повторите");
        }
        return getSendMessage(ownerChatId, builder.toString(), null);
    }

    public String getCaptionForScreenShot(StringBuilder builder, Long chatId) {
        Optional<UserClient> optClient = userClientService.findByChatId(chatId);
        if (optClient.isPresent()) {
            UserClient client = optClient.get();
            builder.setLength(0);

            return builder.append("Пользователь ").append(client.getUserClintName())
                    .append(" отправил скриншот об оплате. Проверте сумму и реквезиты.").toString();

        }

        return "Произошла ошибка. Клиент не найден в базе данных.";
    }


    public SendDocument screenDoc(StringBuilder builder, Long chatId, Long ownerChatId, Message message) {
        SendDocument screen = new SendDocument();
        screen.setChatId(ownerChatId);
        screen.setDocument(new InputFile(message.getDocument().getFileId()));
        screen.setCaption(getCaptionForScreenShot(builder,chatId));
        screen.setReplyMarkup(userKeyboard.getPayKeyboard(chatId));
        return screen;

    }

    public SendPhoto screenPhoto(StringBuilder builder, Long chatId, Long ownerChatId, Message message) {
        SendPhoto screen = new SendPhoto();
        screen.setChatId(ownerChatId);
        screen.setPhoto(new InputFile(message.getPhoto().get(0).getFileId()));
        screen.setCaption(getCaptionForScreenShot(builder, chatId));
        screen.setReplyMarkup(userKeyboard.getPayKeyboard(chatId));
        return screen;

    }
}
