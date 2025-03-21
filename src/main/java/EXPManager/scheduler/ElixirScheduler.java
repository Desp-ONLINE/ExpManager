package EXPManager.scheduler;

import EXPManager.database.PlayerRepository;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ElixirScheduler {

    private static final PlayerRepository repository = PlayerRepository.getInstance();

    public static void startFatigueReductionTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                repository.reduceDuration();
            }
        }.runTaskTimerAsynchronously(Bukkit.getPluginManager().getPlugin("EXPManager"), 20L, 20L);
    }
}
