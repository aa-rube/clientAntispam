package app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "stop_word")
public class StopWordObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String word;
    private Long ownerChatId;
    public Long getOwnerChatId() {
        return ownerChatId;
    }
    public void setOwnerChatId(Long ownerChatId) {
        this.ownerChatId = ownerChatId;
    }
    public String getWord() {
        return word;
    }
    public void setWord(String word) {
        this.word = word;
    }

    public int getId() {
        return id;
    }
}
