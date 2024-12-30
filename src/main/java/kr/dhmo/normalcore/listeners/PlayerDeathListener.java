package kr.dhmo.normalcore.listeners;

import kr.dhmo.normalcore.managers.PlayersManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

final public class PlayerDeathListener implements Listener {
    @EventHandler
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final UUID playerUniqueId = player.getUniqueId();
        final int playerLife = PlayersManager.getLife(playerUniqueId) - 1;

        PlayersManager.setLastDeathAt(playerUniqueId, System.currentTimeMillis());

        if(playerLife == -1) {
            PlayersManager.createTimer(player);
        }
    }
}
