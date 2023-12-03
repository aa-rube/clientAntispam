package app.bot.enviroment.keyboards;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminKeyboard {
    public InlineKeyboardMarkup getOptionsForVipUsers() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton add = new InlineKeyboardButton();
        add.setText("Добавить");
        add.setCallbackData("87");
        firstRow.add(add);

        InlineKeyboardButton list = new InlineKeyboardButton();
        list.setText("Список");
        list.setCallbackData("86");
        firstRow.add(list);

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("◀️ Назад");
        back.setCallbackData("backToStart");
        backRow.add(back);

        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(backRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getBackToVipOption() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("◀️ Назад");
        back.setCallbackData("88");
        backRow.add(back);

        keyboardMatrix.add(backRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }
}
