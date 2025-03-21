package EXPManager;

import EXPManager.database.ConfigRepository;
import EXPManager.database.ElixirRepository;
import EXPManager.database.MonsterRepository;
import EXPManager.database.PlayerRepository;
import EXPManager.listener.EXPListener;
import EXPManager.listener.PlayerJoinAndQuitListener;
import EXPManager.scheduler.ElixirScheduler;
import java.util.Collection;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class EXPManager extends JavaPlugin {

    @Getter
    private static EXPManager instance;

    @Override
    public void onEnable() {
        instance = this;
        ElixirScheduler.startFatigueReductionTask();
        register();

        Bukkit.getPluginManager().registerEvents(new EXPListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinAndQuitListener(), this);
        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        for (Player player : onlinePlayers) {
            PlayerRepository.getInstance().loadPlayerInfo(player);
        }
    }

    @Override
    public void onDisable() {
        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        for (Player player : onlinePlayers) {
            PlayerRepository.getInstance().savePlayerLog(player);
        }
    }

    private void register() {
        ElixirRepository.getInstance();
        MonsterRepository.getInstance();
        PlayerRepository.getInstance();
        ConfigRepository.getInstance();
    }
}
