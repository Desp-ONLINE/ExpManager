package EXPManager.utils;

import EXPManager.EXPManager;
import EXPManager.database.ElixirRepository;
import EXPManager.database.PlayerRepository;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PapiExpansion extends PlaceholderExpansion {

    private EXPManager expManager;

    public PapiExpansion(EXPManager expManager) {
        this.expManager = expManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ExpManager";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Dople";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if(player == null){
            return "";
        }
        final String string = identifier.split("_")[0];
        if(Objects.equals(string, "percentage")) {
            int multiply = PlayerRepository.players.get(player.getUniqueId().toString()).getMultiply();
            return String.valueOf(multiply);
        }
        if(Objects.equals(string, "duration")) {
            int duration = PlayerRepository.players.get(player.getUniqueId().toString()).getLeftDuration();
            return String.valueOf(duration);
        }
        return "";
    }
}
