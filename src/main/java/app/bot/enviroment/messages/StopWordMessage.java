package app.bot.enviroment.messages;

import app.bot.enviroment.keyboards.StopWordKeyBoard;
import app.model.StopWordObject;
import app.service.StopWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Service
public class StopWordMessage {
    @Autowired
    private StopWordKeyBoard stopWordKeyBoard;
    private SendMessage getSendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        msg.setReplyMarkup(markup);
        msg.enableHtml(true);
        msg.setParseMode(ParseMode.HTML);
        return msg;
    }

    public SendMessage getListWords(StringBuilder builder,Long chatId, List<StopWordObject> words) {
        builder.setLength(0);
        builder.append("Cписок стоп слов:\n");

        for (StopWordObject stop : words) {
            builder.append(stop.getWord()).append("\n")
                    .append("/deleteStopWord_").append(stop.getId()).append("\n\n");
        }

        return getSendMessage(chatId, builder.toString(), stopWordKeyBoard.getBackToWordsOptions());
    }

    public SendMessage enterNewStopWord(StringBuilder builder,Long chatId) {
        builder.setLength(0);
        builder.append("Введите новое стоп-слово❌");
        return getSendMessage(chatId, builder.toString(), stopWordKeyBoard.getBackToWordsOptions());
    }

    public SendMessage deletedSuccess(StringBuilder builder,Long chatId) {
        builder.setLength(0);
        builder.append("Удаление прошло успешно!");
        return getSendMessage(chatId, builder.toString(), stopWordKeyBoard.getBackToWordsOptions());
    }

    public SendMessage theListIsEmpty(StringBuilder builder,Long chatId) {
        return getSendMessage(chatId, "Cписок стоп слов пуст!", stopWordKeyBoard.getBackToWordsOptionsOrAddNew());
    }
}
