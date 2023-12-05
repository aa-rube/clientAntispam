package app.bot.enviroment.messages;

import app.bot.enviroment.keyboards.UserKeyboard;
import app.model.UserClient;
import app.service.UserClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserMessage {
    @Autowired
    private UserClientService userClientService;
    @Autowired
    private UserKeyboard keyboard;
    private SendMessage getSendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        msg.setReplyMarkup(markup);
        msg.enableHtml(true);
        msg.setParseMode(ParseMode.HTML);
        return msg;
    }

    public SendMessage getStartMessage(StringBuilder builder,Long chatId) {
        builder.setLength(0);
        builder.append("\uD83D\uDE80Добро пожаловать в AntiSpamBot для телеграм.\n\n")
                .append("Если вы хотите добавить бота в свою группу, то нажмите \"НАЧАТЬ\"!");
        return getSendMessage(chatId, builder.toString(), keyboard.startReg());
    }

    public SendMessage pretendedToPay(StringBuilder builder,Long chatId) {
        builder.setLength(0);
        builder.append("Отлично. Мы создали для вас учетную запись. Тариф на 30 дней.\n\n")
                .append("Для согласования суммы и оплаты напишите @biker_niga\n\n")
                .append("Вы также можете пройти пробный период в течение 5 дней, чтобы оценить все преимущества нашего сервиса.");
        return getSendMessage(chatId, builder.toString(), keyboard.startPay(false));
    }

    public SendMessage waitForScreenShot(StringBuilder builder,Long chatId) {
        builder.setLength(0);
        builder.append("Отправьте скриншот платежа, что бы мы смогли проверить ваши данные и подтвердить оплату");
        return getSendMessage(chatId, builder.toString(), null);
    }

    public SendMessage payUpdate(StringBuilder builder,Long chatUserId, int pay) {
        builder.setLength(0);

        if (pay == 1) {
            Optional<UserClient> clientOpt = userClientService.findByChatId(chatUserId);

            if (clientOpt.isPresent()) {
                UserClient user = clientOpt.get();
                user.setPaid(true);
                user.setTryPeriod(true);
                user.setSubscriptionEnd(user.getSubscriptionEnd().plusDays(30));
                userClientService.save(user);

                builder.append("Спасибо за Ваш платеж.\nВам открыт доступ к боту до ")
                        .append(user.getSubscriptionEnd().toString().substring(0, 10));
                return getSendMessage(chatUserId, builder.toString(), null);
            }
        }


        if (pay == 2) {
            Optional<UserClient> clientOpt = userClientService.findByChatId(chatUserId);

            if (clientOpt.isPresent()) {
                UserClient user = clientOpt.get();
                user.setPaid(true);
                user.setTryPeriod(true);
                user.setSubscriptionEnd(user.getSubscriptionEnd().plusDays(5));
                userClientService.save(user);

                builder.append("Пробный период активирован.\nВам открыт доступ к боту до ")
                        .append(user.getSubscriptionEnd().toString().substring(0, 10));
                return getSendMessage(chatUserId, builder.toString(), null);
            }
        }
        builder.append("Администратор отклонил Ваш платеж. Проверте информацию и попробуйте отправить скриншот еще раз.");
        return getSendMessage(chatUserId,builder.toString(), keyboard.startPay(false));
    }

    public SendMessage waitForCheckThePay(StringBuilder builder, Long chatId) {
        builder.setLength(0);
        builder.append("Oжидайте проверку платежа");
        return getSendMessage(chatId, builder.toString(), null);
    }

    public SendMessage payIsOff(StringBuilder builder, UserClient client) {
        builder.setLength(0);
        builder.append("К сожалению подписка зпкончилась. Для продолжения использования необходимо оплатить подписку.\n")
                .append("Что бы уточнить детали свяжитесь с @biker_niga");

        boolean tryPeriodIsOff = false;
        Optional<UserClient> userClientOptional = userClientService.findByChatId(client.getChatId());
        if (userClientOptional.isPresent()) {
            tryPeriodIsOff = userClientOptional.get().isTryPeriod();
        }

        return getSendMessage(client.getChatId(), builder.toString(), keyboard.startPay(tryPeriodIsOff));
    }

    public SendMessage fiveDaysTryMsg(StringBuilder builder, Long ownerChatId, UserClient userClient) {
        builder.setLength(0);
        builder.append("Пользователь ").append(userClient.getUserClintName()).append(" активировал пробный период до ")
                .append(userClient.getSubscriptionEnd().toString().substring(0,10));
        return getSendMessage(ownerChatId, builder.toString(), null);
    }
}
