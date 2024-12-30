package kr.dhmo.normalcore.listeners;

import kr.dhmo.normalcore.Normalcore;
import kr.dhmo.normalcore.managers.PlayersManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID playerUniqueId = player.getUniqueId();

        if(PlayersManager.getLastDeathAt(playerUniqueId) != 0) {
            if(!PlayersManager.hasTimer(playerUniqueId)) {
                PlayersManager.setLife(playerUniqueId, PlayersManager.getLife(playerUniqueId) - 1);
                Normalcore.respawn(player);
            } else {
                PlayersManager.setTimerPlayer(player);
            }
        }
    }
}
