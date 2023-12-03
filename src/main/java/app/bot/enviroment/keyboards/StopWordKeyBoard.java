package app.bot.enviroment.keyboards;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class StopWordKeyBoard {
    public InlineKeyboardMarkup getBackToWordsOptions() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("◀️ Назад");
        back.setCallbackData("0");
        backRow.add(back);

        keyboardMatrix.add(backRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getBackToWordsOptionsOrAddNew() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        InlineKeyboardButton add = new InlineKeyboardButton();
        add.setText("Добавить");
        add.setCallbackData("addMyNewGroup");

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("◀️ Назад");
        back.setCallbackData("0");

        backRow.add(back);
        backRow.add(add);

        keyboardMatrix.add(backRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }
}