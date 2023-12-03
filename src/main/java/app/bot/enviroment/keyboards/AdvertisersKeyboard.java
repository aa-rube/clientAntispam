package app.bot.enviroment.keyboards;

import app.model.GroupToListen;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdvertisersKeyboard {

    public InlineKeyboardMarkup getMainMenu() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton add = new InlineKeyboardButton();
        add.setText("Добавить Партнера");
        add.setCallbackData("14");

        InlineKeyboardButton list = new InlineKeyboardButton();
        list.setText("Список");
        list.setCallbackData("15");
        firstRow.add(add);
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

    public InlineKeyboardMarkup getBackToMain() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("◀️ Назад");
        back.setCallbackData("13");
        backRow.add(back);

        keyboardMatrix.add(backRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup groupsKeyboards(List<GroupToListen> users, int lastIndex) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        int userSize = users.size();
        int maxIndex = 12;

        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = lastIndex; i < Math.min(lastIndex + maxIndex, userSize); i++) {

            InlineKeyboardButton user = new InlineKeyboardButton();
            user.setText(users.get(i).getGroupUserName());
            user.setCallbackData("setG:" + users.get(i).getGroupUserName());
            row.add(user);
            if ((i + 1) % 3 == 0) {
                keyboardMatrix.add(row);
                row = new ArrayList<>();
            }
        }

        if (!row.isEmpty()) {
            keyboardMatrix.add(row);
        }

        List<InlineKeyboardButton> nextOrPrevious = new ArrayList<>();
        InlineKeyboardButton previous = new InlineKeyboardButton();
        previous.setText("⏮страница");
        previous.setCallbackData("getGroups_" + Math.max(0, lastIndex - maxIndex));

        InlineKeyboardButton next = new InlineKeyboardButton();
        next.setText("страница ⏭");
        int index = Math.max(0, Math.min(userSize - maxIndex, lastIndex + maxIndex));
        next.setCallbackData("getGroups_" + index);

        nextOrPrevious.add(previous);
        nextOrPrevious.add(next);

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("Назад");
        back.setCallbackData("13");
        backRow.add(back);

        keyboardMatrix.add(nextOrPrevious);
        keyboardMatrix.add(backRow);

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;

    }

    public InlineKeyboardMarkup getPostCountKeyboard() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton three = new InlineKeyboardButton();
        three.setText("3 поста");
        three.setCallbackData("dayCount_3_7");

        InlineKeyboardButton ten = new InlineKeyboardButton();
        ten.setText("10 постов");
        ten.setCallbackData("dayCount_10_30");

        InlineKeyboardButton thirty = new InlineKeyboardButton();
        thirty.setText("30 постов");
        thirty.setCallbackData("dayCount_30_30");
        firstRow.add(three);
        firstRow.add(ten);
        firstRow.add(thirty);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton sixty = new InlineKeyboardButton();
        sixty.setText("60 постов");
        sixty.setCallbackData("dayCount_60_30");

        InlineKeyboardButton ninty = new InlineKeyboardButton();
        ninty.setText("90 постов");
        ninty.setCallbackData("dayCount_90_30");
        secondRow.add(sixty);
        secondRow.add(ninty);

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("◀️ Назад");
        back.setCallbackData("13");
        backRow.add(back);

        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(secondRow);
        keyboardMatrix.add(backRow);

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }
}
