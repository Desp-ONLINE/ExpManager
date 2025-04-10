package EXPManager.command;

import EXPManager.database.EventRepository;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ElixirFormulaCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        player.sendMessage("§a 경험치 공식은 다음과 같습니다.");
        player.sendMessage("§f 몬스터 경험치 = §2M§f, 경험치 비약 = §dP§f, 경험치 이벤트 = §aE§f, 추가 경험치 스텟 = §6S§f");
        player.sendMessage("§f 최종 경험치 지급량: (§2M§f*§6S§f)*(§dP§f+§aE)");
        return false;
    }
}
