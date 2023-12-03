package app.bot.enviroment.keyboards;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class OwnersGroupsKeyboard {

    public InlineKeyboardMarkup getBackToMyGroupsMainMenu() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("◀Назад");
        back.setCallbackData("6");
        row.add(back);

        keyboardMatrix.add(row);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }
}
