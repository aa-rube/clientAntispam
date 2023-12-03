package app.service;

import app.model.StopWordObject;
import app.repository.StopWordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StopWordService {
    @Autowired
    private StopWordRepository stopWordRepository;

    public void save(StopWordObject stop) {
        stopWordRepository.save(stop);
    }

    public List<StopWordObject> getAllStopWords() {
        return stopWordRepository.findAll();
    }

    public void deleteById(int id) {
        stopWordRepository.deleteById(id);
    }

    public List<StopWordObject> getAllStopWordsByOwnerChatId(Long ownerChatId) {
        return stopWordRepository.findAllByOwnerChatId(ownerChatId);
    }
}
