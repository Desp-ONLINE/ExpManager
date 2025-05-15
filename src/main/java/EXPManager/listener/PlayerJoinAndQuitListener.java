package EXPManager.listener;

import EXPManager.database.PlayerRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinAndQuitListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerRepository.getInstance().loadPlayerInfo(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerRepository.getInstance().savePlayerLog(event.getPlayer());
        EXPListener.getPlayerExpLog().remove(event.getPlayer().getUniqueId().toString());
        EXPListener.getLatestExpLog().remove(event.getPlayer().getUniqueId().toString());
        EXPListener.getCountExpLog().remove(event.getPlayer().getUniqueId().toString());
    }

}
