package EXPManager.database;

import EXPManager.dto.PlayerDto;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;
import org.bukkit.entity.Player;

public class PlayerRepository {

    private static PlayerRepository instance;
    private final MongoCollection<Document> playerDB;
    public static Map<String, PlayerDto> players = new HashMap<>();

    public PlayerRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.playerDB = database.getDatabase().getCollection("PlayerElixir");
    }

    public static synchronized PlayerRepository getInstance() {
        if (instance == null) {
            instance = new PlayerRepository();
        }
        return instance;
    }

    public void loadPlayerInfo(Player player) {
        String uuid = player.getUniqueId().toString();
        String user_id = player.getName();
        Document document = new Document("uuid", uuid);

        if (playerDB.find(Filters.eq("uuid", uuid)).first() == null) {
            Document newUser = new Document()
                    .append("user_id", user_id)
                    .append("uuid", uuid)
                    .append("leftDuration", 0)
                    .append("multiply", 1)
                    .append("latestUsedItem", "");
            playerDB.insertOne(newUser);
        }
        int leftDuration = playerDB.find(document).first().getInteger("leftDuration");
        int multiply = playerDB.find(document).first().getInteger("multiply");
        String latestUsedItem = playerDB.find(document).first().getString("latestUsedItem");

        PlayerDto playerDto = PlayerDto.builder()
                .user_id(user_id)
                .uuid(uuid)
                .leftDuration(leftDuration)
                .multiply(multiply)
                .latestUsedItem(latestUsedItem)
                .build();

        players.put(uuid, playerDto);
    }

    public void savePlayerLog(Player player) {
        PlayerDto playerDto = players.get(player.getUniqueId().toString());

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
}
