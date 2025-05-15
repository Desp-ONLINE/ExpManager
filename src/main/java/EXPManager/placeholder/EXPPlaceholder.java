package EXPManager.placeholder;

import EXPManager.EXPManager;
import EXPManager.listener.EXPListener;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EXPPlaceholder extends PlaceholderExpansion {

    private final EXPManager expManager;

    public EXPPlaceholder(EXPManager expManager) {
        this.expManager = expManager;
    }

    @Override
    public String getIdentifier() {
        return "EXPPlaceholder";
    }

    @Override
    public String getAuthor() {
        return "Dople";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }
        final String string = identifier.split("_")[0];
        if (Objects.equals(string, "expLog")) {
            return String.valueOf(EXPListener.getPlayerExpLog().get(player.getUniqueId().toString()));
        }
        if(Objects.equals(string, "latestExpLog")) {
            return String.valueOf(EXPListener.getLatestExpLog().get(player.getUniqueId().toString()));
        }
        if(Objects.equals(string, "countExpLog")) {
            return String.valueOf(EXPListener.getCountExpLog().get(player.getUniqueId().toString()));
        }
        return "값 없음";
    }
}
