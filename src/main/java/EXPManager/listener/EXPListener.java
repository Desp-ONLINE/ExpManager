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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class EXPListener implements Listener {

    private final ElixirRepository elixirRepository;
    private final MonsterRepository monsterRepository;
    private final PlayerRepository playerRepository;
    private final ConfigRepository configRepository;

    public EXPListener() {
        this.playerRepository = PlayerRepository.getInstance();
        this.elixirRepository = ElixirRepository.getInstance();
        this.monsterRepository = MonsterRepository.getInstance();
        this.configRepository = ConfigRepository.getInstance();

        // 5초마다 캐시 크기 출력하는 스케줄러 실행
        new BukkitRunnable() {
            @Override
            public void run() {
                int cacheSize = players.size();
                System.out.println("EXPManager playerCache = " + cacheSize);
            }
        }.runTaskTimer(EXPManager.getInstance(), 0L, 100L); // 즉시 실행 후 100틱(5초)마다 반복
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
//        if (players == null) {
//            PlayerRepository playerRepository = PlayerRepository.getInstance();
//            players = playerRepository.getPlayers();
//        }
//        players = PlayerRepository.getInstance().getPlayers();

        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        String user_id = player.getName();

        PlayerDto newDto = playerRepository.getPlayer(uuid, user_id);

        players.put(uuid, newDto);
        //playerRepository.savePlayerLog(uuid, players);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        playerRepository.savePlayerLog(uuid, players);
    }

    @EventHandler
    public void onPlayerUseElixir(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        Map<String, ElixirDto> elixirCache = elixirRepository.getElixirCache();

        String playerRightHandItemId = MMOItems.getID(itemInMainHand);
        if(event.getAction().isRightClick() && elixirCache.containsKey(playerRightHandItemId)) {
            event.setCancelled(true);

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

        if(!(event.getKiller() instanceof Player)){
            return;
        }
        Player player = (Player) event.getKiller();
        String killedMonsterName = event.getMob().getType().getInternalName();

        Map<String, MonsterDto> monsters = monsterRepository.getMonsters();
        if (monsters.containsKey(killedMonsterName)) {
            MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EXPManager.getInstance());
            int monsterLevel = (int) event.getMob().getLevel();
            double mobLevel = event.getMobLevel();
            int playerLevel = mmoCoreAPI.getPlayerData(player).getLevel();

            if ((playerLevel - monsterLevel) >= levelDiffLimit) {
                player.sendActionBar("§c몬스터와의 레벨 차이가 "+levelDiffLimit+" 이상이어서 경험치를 얻을 수 없습니다!");
                return;
            }

            MonsterDto monsterDto = monsters.get(killedMonsterName);
            MMOPlayerData mmoPlayerData = MMOPlayerData.get(player.getUniqueId());
            StatMap statMap = mmoPlayerData.getStatMap();
            double userExpAdditionalStat = statMap.getStat("ADDITIONAL_EXPERIENCE");
            int exp = monsterDto.getExp();
            int v = (int) (exp + exp * (userExpAdditionalStat / 100));

            int rewardExp = v + (v / 100 * getMultiply(event));

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("huds popup %s exp-message 20 %s",player.getName(),"§a+exp §f"+rewardExp));

            mmoCoreAPI.getPlayerData(player).giveExperience(rewardExp, EXPSource.OTHER);
        }
    }
}
