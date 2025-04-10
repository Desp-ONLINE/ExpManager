package EXPManager.scheduler;

import EXPManager.database.EventRepository;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class EXPEventScheduler {

    private static final EventRepository repository = EventRepository.getInstance();

    public static void startEventDurationReduceTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                repository.reduceDuration();
            }
        }.runTaskTimerAsynchronously(Bukkit.getPluginManager().getPlugin("EXPManager"), 20L, 20L);
    }
}
