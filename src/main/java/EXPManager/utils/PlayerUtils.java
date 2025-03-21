package EXPManager.utils;

import static EXPManager.database.PlayerRepository.players;

import EXPManager.dto.PlayerDto;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.entity.Player;
public class PlayerUtils {

    public static int getMultiply(MythicMobDeathEvent event) {
        Player player = (Player) event.getKiller();
        String uuid = player.getUniqueId().toString();
        PlayerDto playerDto = players.get(uuid);
        return playerDto.getMultiply();
    }
}
