package app.handle;

import app.model.StopWordObject;
import app.service.StopWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StopWordHandle {
    @Autowired
    private StopWordService stopWordService;

    public boolean saveTheWord(Long chatId, String text) {
        try {

            if (text.split(",").length > 2) {
                for (String word : text.split(",")) {
                    StopWordObject object = new StopWordObject();
                    object.setOwnerChatId(chatId);
                    object.setWord(word.trim());
                    stopWordService.save(object);
                }

            } else {
                StopWordObject object = new StopWordObject();
                object.setOwnerChatId(chatId);
                object.setWord(text.trim());
                stopWordService.save(object);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteTheWord(String text) {
        try {
            int id = Integer.parseInt(text.split("_")[1]);
            stopWordService.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
