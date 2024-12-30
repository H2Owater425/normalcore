package kr.dhmo.normalcore;

import kr.dhmo.normalcore.commands.ConfigurationCommand;
import kr.dhmo.normalcore.commands.LifeCommand;
import kr.dhmo.normalcore.commands.tabCompleters.ConfigurationCommandTabCompleter;
import kr.dhmo.normalcore.commands.tabCompleters.LifeCommandTabCompleter;
import kr.dhmo.normalcore.listeners.PlayerDeathListener;
import kr.dhmo.normalcore.listeners.PlayerGamemodeChangeListener;
import kr.dhmo.normalcore.listeners.PlayerJoinListener;
import kr.dhmo.normalcore.listeners.PlayerQuitListener;
import kr.dhmo.normalcore.managers.ConfigurationManager;
import kr.dhmo.normalcore.managers.PlayersManager;
import kr.dhmo.normalcore.utilities.FormatUtility;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.ServerTickManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.UUID;
import java.util.logging.Logger;

public final class Normalcore extends JavaPlugin {
    final public static String errorColor = "§c";
    final private static ServerTickManager serverTickManager = Bukkit.getServerTickManager();
    private static Normalcore instance;

    public static void setServerTickFrozen(boolean isFreeze) {
        Normalcore.serverTickManager.setFrozen(isFreeze);
    }

    public static void respawn(@NotNull Player player) {
        new BukkitRunnable() {
            public void run() {
                final UUID playerUniqueId = player.getUniqueId();
                final int respawnPenaltyTime = ConfigurationManager.getRespawnPenaltyTime() * 20;
                Location location = player.getRespawnLocation();

                if(location == null) {
                    location = Bukkit.getWorlds().getFirst().getSpawnLocation();
                }

                PlayersManager.setLastDeathAt(playerUniqueId, 0);

                player.spigot().respawn();
                player.setGameMode(GameMode.SURVIVAL);
                player.teleport(location);


                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1200, 1, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, respawnPenaltyTime, 0, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, respawnPenaltyTime, 2, false, false));
                player.sendMessage("You have " + FormatUtility.pluralize(PlayersManager.getLife(playerUniqueId), "life") + " left");
            }
        }.runTask(Normalcore.instance);
    }

    public static Logger logger;
    public static PluginManager pluginManager;

    public Normalcore() {
        Normalcore.logger = this.getLogger();
        Normalcore.pluginManager = this.getServer().getPluginManager();
        Normalcore.instance = this;
    }

    @Override
    public void onEnable() {
        final File dataFolder = this.getDataFolder();

        if(!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        ConfigurationManager.load(new File(dataFolder, "configuration.yml"));
        PlayersManager.load(new File(dataFolder, "players.yml"));

        PluginCommand lifeCommand = this.getCommand("life");

        if(lifeCommand != null) {
            lifeCommand.setExecutor(new LifeCommand());
            lifeCommand.setTabCompleter(new LifeCommandTabCompleter());
        } else {
            Normalcore.logger.severe("Life command must exist");
            Normalcore.pluginManager.disablePlugin(this);

            return;
        }

        PluginCommand configurationCommand = this.getCommand("configuration");

        if(configurationCommand != null) {
            configurationCommand.setExecutor(new ConfigurationCommand());
            configurationCommand.setTabCompleter(new ConfigurationCommandTabCompleter());
        } else {
            Normalcore.logger.severe("Configuration command must exist");
            Normalcore.pluginManager.disablePlugin(this);

            return;
        }

        Normalcore.pluginManager.registerEvents(new PlayerDeathListener(), this);
        Normalcore.pluginManager.registerEvents(new PlayerGamemodeChangeListener(), this);
        Normalcore.pluginManager.registerEvents(new PlayerJoinListener(), this);
        Normalcore.pluginManager.registerEvents(new PlayerQuitListener(), this);

        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    UUID playerUniqueId = player.getUniqueId();

                    if(PlayersManager.hasTimer(playerUniqueId)) {
                        PlayersManager.tickTimer(player);
                    }
                }
            }
        }.runTaskTimerAsynchronously(this, 0L, 20L);

        Normalcore.logger.info("Enabled plugin");
    }

    @Override
    public void onDisable() {
        try {
            ConfigurationManager.save();
            PlayersManager.save();
        } catch(Exception exception) {
            Normalcore.logger.severe(exception.getMessage());
        }

        Normalcore.logger.info("Disabled plugin");
    }
}
