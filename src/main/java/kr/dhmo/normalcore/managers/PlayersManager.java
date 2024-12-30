package kr.dhmo.normalcore.managers;

import kr.dhmo.normalcore.Normalcore;
import kr.dhmo.normalcore.utilities.FormatUtility;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

final public class PlayersManager {
    static private final class Timer {
        final private UUID playerUniqueId;
        final private long respawnWaitingTime;
        final private long lastDeathAt;
        final private BossBar bossBar;

        @Contract(pure = true)
        private static @NotNull String getTitle(long remainingRespawnWaitingTime) {
            return FormatUtility.approximateTime(remainingRespawnWaitingTime) + " to respawn";
        }

        public Timer(@NotNull Player player) {
            this.playerUniqueId = player.getUniqueId();
            this.respawnWaitingTime = ConfigurationManager.getRespawnWaitingTime() * 1000;
            this.lastDeathAt = PlayersManager.getLastDeathAt(this.playerUniqueId);
            this.bossBar = Bukkit.createBossBar(Timer.getTitle(respawnWaitingTime), BarColor.PURPLE, BarStyle.SOLID);

            this.setPlayer(player);
        }

        public void setPlayer(@NotNull Player player) {
            this.bossBar.removeAll();
            this.bossBar.addPlayer(player);
        }

        public boolean tick() {
            final long remainingTime = this.respawnWaitingTime - System.currentTimeMillis() + this.lastDeathAt;

            if(remainingTime > 0) {
                this.bossBar.setProgress((double)remainingTime / this.respawnWaitingTime);
                this.bossBar.setTitle(Timer.getTitle(remainingTime));

                return false;
            } else {
                return true;
            }
        }

        public void destroy() {
            this.bossBar.removeAll();
            PlayersManager.timers.remove(playerUniqueId);
        }
    }

    @Contract(pure = true)
    public static @Nullable OfflinePlayer getOfflinePlayer(@NotNull String name) {
        for(OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            final String playerName = offlinePlayer.getName();

            if(playerName != null && playerName.equalsIgnoreCase(name)) {
                return offlinePlayer;
            }
        }

        return null;
    }

    @Contract(pure = true)
    public static @NotNull List<String> getOfflinePlayerNames() {
        List<String> offlinePlayerNames = new ArrayList<>();

        for(OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            offlinePlayerNames.add(offlinePlayer.getName());
        }

        return offlinePlayerNames;
    }

    private static long lastModifiedAt;
    private static File file;
    private static FileConfiguration fileConfiguration;
    final private static HashMap<UUID, Timer> timers = new HashMap<>();

    public static void load(File file) {
        PlayersManager.file = file;
        PlayersManager.fileConfiguration = YamlConfiguration.loadConfiguration(file);
        PlayersManager.lastModifiedAt = file.lastModified();
    }

    public static void save() throws IOException {
        if(PlayersManager.lastModifiedAt >= file.lastModified()) {
            PlayersManager.fileConfiguration.save(PlayersManager.file);
            Normalcore.logger.info("Player data has been saved");

//            return true;
        } else {
            final String path = PlayersManager.file.getAbsolutePath();

            PlayersManager.fileConfiguration.save(new File(path.substring(0, path.length() - 3) + System.currentTimeMillis() + ".yml"));
            Normalcore.logger.warning("Player data backup has been saved instead");

//            return false;
        }
    }

    private static void updateLastModifiedAt() {
        PlayersManager.lastModifiedAt = System.currentTimeMillis();
    }

    public static void setLife(@NotNull UUID playerUniqueId, int life) {
        PlayersManager.fileConfiguration.set(playerUniqueId + ".life", life > 0 ? life : null);
        PlayersManager.updateLastModifiedAt();
    }

    public static int getLife(@NotNull UUID playerUniqueId) {
        return PlayersManager.fileConfiguration.getInt(playerUniqueId + ".life", 0);
    }

    public static void setLastDeathAt(@NotNull UUID playerUniqueId, long lastDeathAt) {
        PlayersManager.fileConfiguration.set(playerUniqueId + ".lastDeathAt", lastDeathAt > 0 ? lastDeathAt : null);
        PlayersManager.updateLastModifiedAt();
    }

    public static long getLastDeathAt(@NotNull UUID playerUniqueId) {
        return PlayersManager.fileConfiguration.getLong(playerUniqueId + ".lastDeathAt", 0);
    }

    public static boolean hasTimer(@NotNull UUID playerUniqueId) {
        return timers.containsKey(playerUniqueId);
    }

    public static void createTimer(@NotNull Player player) {
        PlayersManager.timers.put(player.getUniqueId(), new Timer(player));
    }

    public static void setTimerPlayer(@NotNull Player player) {
        final Timer timer = PlayersManager.timers.get(player.getUniqueId());

        if(timer != null) {
            timer.setPlayer(player);
        }
    }

    public static void tickTimer(@NotNull Player player) {
        final UUID playerUniqueId = player.getUniqueId();
        final Timer timer = PlayersManager.timers.get(playerUniqueId);

        if(timer.tick() && player.getGameMode() == GameMode.SPECTATOR) {
            PlayersManager.setLastDeathAt(playerUniqueId, 0);
            timer.destroy();
            Normalcore.respawn(player);
        }
    }
}
