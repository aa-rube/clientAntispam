package app.model;

public class MessageToDelete {
    int messageId;
    Long chatId;

    public MessageToDelete(int messageId, Long chatId) {
        this.messageId = messageId;
        this.chatId = chatId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
