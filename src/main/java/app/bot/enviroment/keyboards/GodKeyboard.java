package app.bot.enviroment.keyboards;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class GodKeyboard {
    public InlineKeyboardMarkup getStart() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton list = new InlineKeyboardButton();
        list.setText("Список пользователей");
        list.setCallbackData("clientList");
        firstRow.add(list);


        keyboardMatrix.add(firstRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getBack() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton list = new InlineKeyboardButton();
        list.setText("◀Назад");
        list.setCallbackData("backToStart");
        firstRow.add(list);

        keyboardMatrix.add(firstRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

}
