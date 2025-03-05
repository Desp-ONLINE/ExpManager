package EXPManager.database;

import EXPManager.dto.PlayerDto;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;

public class PlayerRepository {

    private static PlayerRepository instance;
    private final MongoCollection<Document> playerDB;
    public static Map<String, PlayerDto> players = new HashMap<>();

    public PlayerRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.playerDB = database.getDatabase().getCollection("PlayerElixir");
//        loadPlayerInfo();
    }

    public static synchronized PlayerRepository getInstance() {
        if (instance == null) {
            instance = new PlayerRepository();
        }
        return instance;
    }

    public void loadPlayerInfo() {
        FindIterable<Document> documents = playerDB.find();
        for (Document document : documents) {
            String user_id = document.getString("user_id");
            String uuid = document.getString("uuid");

            PlayerDto playerDto = PlayerDto.builder()
                    .user_id(user_id)
                    .uuid(uuid)
                    .leftDuration(document.getInteger("leftDuration"))
                    .multiply(document.getInteger("multiply"))
                    .latestUsedItem("latestUsedItem")
                    .build();

            players.put(uuid, playerDto);
        }
    }

    public void savePlayerLog(String uuid, Map<String, PlayerDto> players) {
        PlayerDto playerDto = players.get(uuid);
        Document playerDocument = new Document()
                .append("user_id", playerDto.getUser_id())
                .append("uuid", playerDto.getUuid())
                .append("leftDuration", playerDto.getLeftDuration())
                .append("multiply", playerDto.getMultiply())
                .append("latestUsedItem", playerDto.getLatestUsedItem());

        playerDB.replaceOne(
                Filters.eq("uuid", playerDto.getUuid()),
                playerDocument,
                new ReplaceOptions().upsert(true)
        );
    }

    public void reduceDuration() {
        for (String uuid : players.keySet()) {
            PlayerDto playerDto = players.get(uuid);
            int newDuration = Math.max(playerDto.getLeftDuration() - 1, 0);
            if (newDuration == 0) {
                playerDto.setMultiply(0);
            }

            playerDto.setLeftDuration(newDuration);
            players.replace(uuid, playerDto);
        }
    }

    public Map<String, PlayerDto> getPlayers() {
        return players;
    }

    public PlayerDto getPlayer(String uuid, String user_id) {
        Document query = playerDB.find(Filters.eq("uuid", uuid)).first();

        if (query != null) {
            String userId = query.getString("user_id");
            int leftDuration = query.getInteger("leftDuration");
            return PlayerDto.builder().user_id(userId).uuid(uuid).leftDuration(leftDuration).multiply(query.getInteger("multiply")).latestUsedItem(query.getString("latestUsedItem")).build();
        } else {
            PlayerDto newDto = PlayerDto.builder().user_id(user_id).uuid(uuid).leftDuration(0).multiply(1).latestUsedItem("default").build();
            players.put(uuid, newDto);
            savePlayerLog(uuid, players);
            return newDto;
        }
    }
}
