package EXPManager.database;

import EXPManager.dto.MonsterDto;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;

public class MonsterRepository {

    private static MonsterRepository instance;
    private static Map<String, MonsterDto> monsters = new HashMap<>();
    private final MongoCollection<Document> monsterDB;

    public MonsterRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.monsterDB = database.getDatabase().getCollection("Monster");
        loadMonsters();
    }

    public static synchronized MonsterRepository getInstance() {
        if (instance == null) {
            instance = new MonsterRepository();
        }
        return instance;
    }

    public void loadMonsters() {
        FindIterable<Document> documents = monsterDB.find();
        for (Document document : documents) {
            String mythicMobId = document.getString("mythicMobID");
            Integer exp = document.getInteger("exp");

            MonsterDto monsterDto = MonsterDto.builder()
                    .mythicMobId(mythicMobId)
                    .exp(exp).build();

            monsters.put(mythicMobId, monsterDto);
        }
    }

    public Map<String, MonsterDto> getMonsters() {
        return monsters;
    }
}
