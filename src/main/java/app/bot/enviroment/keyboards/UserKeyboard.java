package app.bot.enviroment.keyboards;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserKeyboard {
    public InlineKeyboardMarkup startReg() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton msg = new InlineKeyboardButton();

        msg.setText("НАЧАТЬ");
        msg.setCallbackData("letsReg");

        firstRow.add(msg);
        keyboardMatrix.add(firstRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup startPay() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton msg = new InlineKeyboardButton();

        msg.setText("Отправить скриншот об оплате");
        msg.setCallbackData("sendPayScreen");

        firstRow.add(msg);
        keyboardMatrix.add(firstRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public ReplyKeyboard getPayKeyboard(Long chatId) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton ok = new InlineKeyboardButton();
        ok.setText("Подвердить");
        ok.setCallbackData("pay_" + chatId);

        InlineKeyboardButton no = new InlineKeyboardButton();
        no.setText("Отклонить");
        no.setCallbackData("pay_" + chatId + "_0");

        firstRow.add(ok);
        firstRow.add(no);

        keyboardMatrix.add(firstRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }
}
