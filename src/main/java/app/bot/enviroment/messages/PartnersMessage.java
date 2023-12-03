package app.bot.enviroment.messages;

import app.model.AdvUser;
import org.springframework.stereotype.Service;

@Service
public class PartnersMessage {

    public String userTimeEndMsgToUser(StringBuilder builder,AdvUser user) {
        builder.setLength(0);
        return builder.append("Время на публикацию рекламы в группе ")
                .append(user.getPermissionToGroup()).append(" закончилось.\n\nПо вопросам сотрудничества:")
                .append(user.getAdminUserNameOwner())
                .append("\n\n❗Если Ваше сообщение удалено, то скорее всего мы определи его как спам.").toString();
    }

    public String userTimeEndMsgToAdmin(StringBuilder builder,AdvUser user) {
        builder.setLength(0);
        return builder.append("У ").append(user.getUserName())
                .append(" закончился срок размещения рекламы в группе ").append(user.getPermissionToGroup()).toString();
    }

    public String postCountEndMsgToUser(StringBuilder builder,AdvUser user) {
        builder.setLength(0);
        return builder.append("Вы исчерпали все количество постов в группе ")
                .append(user.getPermissionToGroup()).append("\n")
                .append("По вопросам сотрудничества: ").append(user.getAdminUserNameOwner())
                .append("\n\n❗Если Ваше сообщение удалено, то скорее всего мы определи его как спам.").toString();
    }

    public String postCountEndMsgToAdmin(StringBuilder builder,AdvUser user) {
        builder.setLength(0);
        return builder.append("У ").append(user.getUserName()).append(" исчерпано количество постов для группы ")
                .append(user.getPermissionToGroup()).toString();
    }

    public String postCounterMsgToUser(StringBuilder builder,AdvUser user, int count) {
        builder.setLength(0);
        return builder.append("У вас осталось ").append(count).append(" постов до ")
                .append(user.getEndPermission().toString().substring(0, 16)).append("\n")
                .append("в группе ").append(user.getPermissionToGroup()).append("\n\n")
                .append("Если пост списался ошибочно, отправьте пожалуйста \"+\" менеджеру ")
                .append(user.getAdminUserNameOwner()).append(" чтобы вернуть списанный пост.").toString();
    }
}
