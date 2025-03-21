package EXPManager.database;

import EXPManager.dto.ElixirDto;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;

public class ElixirRepository {

    private static ElixirRepository instance;
    private final MongoCollection<Document> elixir;
    private final Map<String, ElixirDto> elixirCache = new HashMap<>();

    private ElixirRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.elixir = database.getDatabase().getCollection("Elixir");
        loadElixirs();
    }

    public static synchronized ElixirRepository getInstance() {
        if (instance == null) {
            instance = new ElixirRepository();
        }
        return instance;
    }

    private void loadElixirs() {
        FindIterable<Document> documents = elixir.find();
        for (Document document : documents) {
            String mmoItemId = document.getString("mmoItemID");

            ElixirDto elixirDto = ElixirDto.builder()
                    .mmoItemId(mmoItemId)
                    .duration(document.getInteger("duration"))
                    .multiply(document.getInteger("multiply"))
                    .build();
            elixirCache.put(mmoItemId, elixirDto);
        }
    }

    public Map<String, ElixirDto> getElixirCache() {
        return elixirCache;
    }

}
