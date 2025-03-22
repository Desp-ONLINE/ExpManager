package EXPManager.listener;

import static EXPManager.database.PlayerRepository.players;
import static EXPManager.utils.PlayerUtils.getMultiply;

import EXPManager.EXPManager;
import EXPManager.database.ConfigRepository;
import EXPManager.database.ElixirRepository;
import EXPManager.database.MonsterRepository;
import EXPManager.database.PlayerRepository;
import EXPManager.dto.ConfigDto;
import EXPManager.dto.ElixirDto;
import EXPManager.dto.MonsterDto;
import EXPManager.dto.PlayerDto;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.StatMap;

import java.util.Map;

import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EXPListener implements Listener {

    public ElixirRepository elixirRepository;
    public MonsterRepository monsterRepository;
    public PlayerRepository playerRepository;
    public ConfigRepository configRepository;

    public EXPListener() {
        this.playerRepository = PlayerRepository.getInstance();
        this.elixirRepository = ElixirRepository.getInstance();
        this.monsterRepository = MonsterRepository.getInstance();
        this.configRepository = ConfigRepository.getInstance();
    }

    @EventHandler
    public void onPlayerUseElixir(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        Map<String, ElixirDto> elixirCache = elixirRepository.getElixirCache();

        String playerRightHandItemId = MMOItems.getID(itemInMainHand);
        if (event.getAction().isRightClick() && elixirCache.containsKey(playerRightHandItemId)) {
            event.setCancelled(true);

            if (itemInMainHand.getAmount() > 1) {
                itemInMainHand.setAmount(itemInMainHand.getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            }

            ElixirDto elixirDto = elixirCache.get(playerRightHandItemId);
            int duration = elixirDto.getDuration();

            PlayerDto playerDto = PlayerDto.builder()
                    .user_id(player.getName())
                    .uuid(player.getUniqueId().toString())
                    .leftDuration(duration)
                    .multiply(elixirDto.getMultiply())
                    .latestUsedItem(playerRightHandItemId)
                    .build();

            players.put(playerDto.getUuid(), playerDto);
        }
    }

    @EventHandler
    public void onPlayerKillMonster(MythicMobDeathEvent event) {

        ConfigDto configDto = configRepository.getConfigDto();
        int levelDiffLimit = configDto.getLevelDiffLimit();

        if (!(event.getKiller() instanceof Player player)) {
            return;
        }
        String killedMonsterName = event.getMob().getType().getInternalName();

        Map<String, MonsterDto> monsters = monsterRepository.getMonsters();
        if (monsters.containsKey(killedMonsterName)) {
            MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EXPManager.getInstance());

            double mobLevel = event.getMobLevel();
            int playerLevel = mmoCoreAPI.getPlayerData(player).getLevel();


            MonsterDto monsterDto = monsters.get(killedMonsterName);
            MMOPlayerData mmoPlayerData = MMOPlayerData.get(player.getUniqueId());
            StatMap statMap = mmoPlayerData.getStatMap();

            double userExpAdditionalStat = statMap.getStat("ADDITIONAL_EXPERIENCE");
            int basicExp = monsterDto.getExp();
            int elixirMultiply = players.get(player.getUniqueId().toString()).getMultiply();

            int statExp = (int) (basicExp + (basicExp * userExpAdditionalStat / 100));

            int elixirExp = (statExp + (statExp * elixirMultiply / 100));


            if (Math.abs(mobLevel - playerLevel) >= levelDiffLimit) {
                player.sendActionBar("§c몬스터와의 레벨 차이가 " + levelDiffLimit + " 이상이어서 경험치가 50% 감소되어 지급됩니다!");
                elixirExp /= 2;
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("huds popup %s exp-message 20 %s", player.getName(), "§a+exp §f" + elixirExp));

            mmoCoreAPI.getPlayerData(player).giveExperience(elixirExp, EXPSource.OTHER);
        }
    }
}
