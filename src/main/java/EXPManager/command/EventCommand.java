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

public class EventCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        if(!player.isOp()){
            return false;
        }
        if(strings.length <= 1){
            player.sendMessage("§c잘못된 용법: /경험치이벤트 <배수(%단위)> <시간(초단위)> ");
            return false;
        }
        Integer multiply = Integer.valueOf(strings[0]);
        Integer duration = Integer.valueOf(strings[1]);
        EventRepository.getInstance().startEvent(multiply, duration);
        TextComponent text = Component.text("§a 경험치 이벤트가 시작되었습니다! §f" + duration + "§a초 동안 §f" + multiply + "% §a만큼 경험치가 추가지급 됩니다.");
        Bukkit.getServer().broadcast(text);
        return true;
    }
}
