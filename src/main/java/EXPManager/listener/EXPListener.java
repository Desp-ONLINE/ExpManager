package EXPManager.listener;

import static EXPManager.database.PlayerRepository.players;
import static EXPManager.utils.PlayerUtils.getMultiply;

import EXPManager.EXPManager;
import EXPManager.database.*;
import EXPManager.dto.ConfigDto;
import EXPManager.dto.ElixirDto;
import EXPManager.dto.MonsterDto;
import EXPManager.dto.PlayerDto;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.StatMap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import lombok.Getter;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.dople.dataSync.inventory.InventorySyncListener;
import org.dople.levelUpBurning.api.LevelUpBurningAPI;

public class EXPListener implements Listener {

    public ElixirRepository elixirRepository;
    public MonsterRepository monsterRepository;
    public PlayerRepository playerRepository;
    public ConfigRepository configRepository;

    @Getter
    private static HashMap<String, Integer> playerExpLog = new HashMap<>();
    @Getter
    private static Map<String, Integer> latestExpLog = new HashMap<>();
    @Getter
    private static Map<String, Integer> countExpLog = new HashMap<>();

    private static EXPListener instance;

    public static EXPListener getInstance() {
        if (instance == null) {
            instance = new EXPListener();
        }
        return instance;
    }

    public EXPListener() {
        this.playerRepository = PlayerRepository.getInstance();
        this.elixirRepository = ElixirRepository.getInstance();
        this.monsterRepository = MonsterRepository.getInstance();
        this.configRepository = ConfigRepository.getInstance();
    }

    @EventHandler
    public void onPlayerUseElixir(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        EquipmentSlot hand = event.getHand();
        if (hand != null && hand != EquipmentSlot.HAND) {
            return;
        }

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        Map<String, ElixirDto> elixirCache = elixirRepository.getElixirCache();

        String playerRightHandItemId = MMOItems.getID(itemInMainHand);
        if (event.getAction().isRightClick() && elixirCache.containsKey(playerRightHandItemId)) {
            event.setCancelled(true);
            if(InventorySyncListener.isDataLoading(player)){
                player.sendMessage("§c 데이터가 로드중입니다.");
                return;
            }

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

            double burningBonus = 0.0;
            if (playerLevel <= 100) {
                PlayerClass profess = mmoCoreAPI.getPlayerData(player).getProfess();
                String burningClassId = LevelUpBurningAPI.getBurningClassId(player.getUniqueId());
                if (profess.getName().equals(burningClassId)) {
                    burningBonus = 1.00;
                }
            }

            if (playerLevel <= 10) {
                basicExp = (int) (basicExp + basicExp * (2.20 + burningBonus));
            } else if (playerLevel <= 20) {
                basicExp = (int) (basicExp + basicExp * (2.10 + burningBonus));
            } else if (playerLevel <= 30) {
                basicExp = (int) (basicExp + basicExp * (2.00 + burningBonus));
            } else if (playerLevel <= 40) {
                basicExp = (int) (basicExp + basicExp * (1.80 + burningBonus));
            } else if (playerLevel <= 50) {
                basicExp = (int) (basicExp + basicExp * (1.70 + burningBonus));
            } else if (playerLevel <= 60) {
                basicExp = (int) (basicExp + basicExp * (1.60 + burningBonus));
            } else if (playerLevel <= 70) {
                basicExp = (int) (basicExp + basicExp * (1.55 + burningBonus));
            } else if (playerLevel <= 80) {
                basicExp = (int) (basicExp + basicExp * (1.50 + burningBonus));
            } else if (playerLevel <= 90) {
                basicExp = (int) (basicExp + basicExp * (1.45 + burningBonus));
            } else if (playerLevel <= 100) {
                basicExp = (int) (basicExp + basicExp * (1.10 + burningBonus));
            } else if (playerLevel <= 130) {
                basicExp = (int) (basicExp + basicExp * 0.70);
            }


            int elixirMultiply = players.get(player.getUniqueId().toString()).getMultiply();
            elixirMultiply += EventRepository.getMultiply();

            int statExp = (int) (basicExp + (basicExp * userExpAdditionalStat / 100));

            int elixirExp = (statExp + (statExp * elixirMultiply / 100));


            if (Math.abs(mobLevel - playerLevel) >= levelDiffLimit) {
//            if (mobLevel >= playerLevel + levelDiffLimit) {
                player.sendActionBar("§c몬스터와의 레벨 차이가 " + levelDiffLimit + " 이상이어서 경험치가 80% 감소되어 지급됩니다!");
                elixirExp *= 0.2;
            }
            int resultExp = elixirExp;
            if (playerExpLog.containsKey(player.getUniqueId().toString())) {
                resultExp = playerExpLog.get(player.getUniqueId().toString()) + elixirExp;
            }
            if (!countExpLog.containsKey(player.getUniqueId().toString())) {
                countExpLog.put(player.getUniqueId().toString(), 0);
            }
            playerExpLog.put(player.getUniqueId().toString(), resultExp);
            latestExpLog.put(player.getUniqueId().toString(), elixirExp);
            countExpLog.put(player.getUniqueId().toString(), countExpLog.get(player.getUniqueId().toString()) + 1);

//            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hud pop show " + player.getName() + " exp_popup");


            mmoCoreAPI.getPlayerData(player).giveExperience(elixirExp, EXPSource.OTHER);
        }
    }
}
