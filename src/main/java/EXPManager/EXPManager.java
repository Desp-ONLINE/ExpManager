package EXPManager;

import static EXPManager.database.PlayerRepository.players;

import EXPManager.database.ConfigRepository;
import EXPManager.database.ElixirRepository;
import EXPManager.database.MonsterRepository;
import EXPManager.database.PlayerRepository;
import EXPManager.dto.PlayerDto;
import EXPManager.listener.EXPListener;
import EXPManager.scheduler.ElixirScheduler;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class EXPManager extends JavaPlugin {

    @Getter
    private static EXPManager instance;
    @Getter
    private static Map<String, PlayerDto> firstCache = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(new EXPListener(), this);
        ElixirScheduler.startFatigueReductionTask();
        register();

        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        PlayerRepository repository = PlayerRepository.getInstance();

        for (Player player : onlinePlayers) {
            String user_id = player.getName();
            String uuid = player.getUniqueId().toString();
            PlayerDto playerDto = repository.getPlayer(uuid, user_id);

            firstCache.put(user_id, playerDto);
        }
    }

    @Override
    public void onDisable() {
        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        PlayerRepository repository = PlayerRepository.getInstance();
        for (Player player : onlinePlayers) {
            String uuid = player.getUniqueId().toString();
            if (players.get(uuid) == null) {
                PlayerDto playerDto = repository.getPlayer(uuid, player.getName());
                players.put(uuid, playerDto);
            }
            repository.savePlayerLog(uuid, players);
        }
    }

    private void register() {
        ElixirRepository.getInstance();
        MonsterRepository.getInstance();
        PlayerRepository.getInstance();
        ConfigRepository.getInstance();
    }
}
