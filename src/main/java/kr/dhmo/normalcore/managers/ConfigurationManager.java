package kr.dhmo.normalcore.managers;

import kr.dhmo.normalcore.Normalcore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

final public class ConfigurationManager {
    private static long lastModifiedAt;
    private static File file;
    private static FileConfiguration fileConfiguration;

    public static void load(File file) {
        ConfigurationManager.file = file;
        ConfigurationManager.fileConfiguration = YamlConfiguration.loadConfiguration(file);
        ConfigurationManager.lastModifiedAt = file.lastModified();
    }

    public static void reload() {
        ConfigurationManager.fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public static void save() throws IOException {
        if(ConfigurationManager.lastModifiedAt >= file.lastModified()) {
            ConfigurationManager.fileConfiguration.save(ConfigurationManager.file);
            Normalcore.logger.info("Configuration data has been saved");

//            return true;
        } else {
            final String path = ConfigurationManager.file.getAbsolutePath();

            ConfigurationManager.fileConfiguration.save(new File(path.substring(0, path.length() - 3) + System.currentTimeMillis() + ".yml"));
            Normalcore.logger.warning("Configuration data backup has been saved instead");

//            return false;
        }
    }

    private static void updateLastModifiedAt() {
        ConfigurationManager.lastModifiedAt = System.currentTimeMillis();
    }

    public static void setRespawnWaitingTime(long respawnWaitingTime) {
        ConfigurationManager.fileConfiguration.set("respawnWaitingTime", respawnWaitingTime > 0 && respawnWaitingTime != 10800 ? respawnWaitingTime : null);
        ConfigurationManager.updateLastModifiedAt();
    }

    public static long getRespawnWaitingTime() {
        return ConfigurationManager.fileConfiguration.getLong("respawnWaitingTime", 10800);
    }

    public static void setRespawnPenaltyTime(int respawnPenaltyTime) {
        ConfigurationManager.fileConfiguration.set("respawnPenaltyTime", respawnPenaltyTime > 0 ? respawnPenaltyTime : null);
    }

    public static int getRespawnPenaltyTime() {
        return ConfigurationManager.fileConfiguration.getInt("respawnPenaltyTime", 1800);
    }

    public static void setLifePrice(int lifePrice) {
        ConfigurationManager.fileConfiguration.set("lifePrice", lifePrice > 0 ? lifePrice : null);
        ConfigurationManager.updateLastModifiedAt();
    }

    public static int getLifePrice() {
        return ConfigurationManager.fileConfiguration.getInt("lifePrice", 1);
    }
}
