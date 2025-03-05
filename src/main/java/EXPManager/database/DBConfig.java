package EXPManager.database;

import EXPManager.EXPManager;
import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;

public class DBConfig {

    public String getMongoConnectionContent(){
        File file = new File(EXPManager.getInstance().getDataFolder().getPath() + "/config.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        String url = yml.getString("mongodb.url");
        int port = yml.getInt("mongodb.port");
        String address = yml.getString("mongodb.address");

        return String.format("%s%s:%s/EXPManager", url,address, port);
    }
}
