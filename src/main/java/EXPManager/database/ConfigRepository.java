package EXPManager.database;

import EXPManager.dto.ConfigDto;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class ConfigRepository {

    private static ConfigRepository instance;
    private final MongoCollection<Document> config;
    private static ConfigDto configDto;

    public ConfigRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.config = database.getDatabase().getCollection("config");
        loadConfig();
    }

    public static synchronized ConfigRepository getInstance() {
        if (instance == null) {
            instance = new ConfigRepository();
        }
        return instance;
    }

    private void loadConfig() {
        FindIterable<Document> documents = config.find();
        for (Document document : documents) {
            Integer levelDiffLimit = document.getInteger("levelDiffLimit");

            configDto = ConfigDto.builder()
                    .levelDiffLimit(levelDiffLimit)
                    .build();
        }
    }

    public ConfigDto getConfigDto() {
        return configDto;
    }
}
