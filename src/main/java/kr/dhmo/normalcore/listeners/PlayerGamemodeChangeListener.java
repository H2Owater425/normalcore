package kr.dhmo.normalcore.listeners;

import kr.dhmo.normalcore.Normalcore;
import kr.dhmo.normalcore.managers.PlayersManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import java.util.UUID;

final public class PlayerGamemodeChangeListener implements Listener {
    @EventHandler
    public void onPlayerGamemodeChange(PlayerGameModeChangeEvent event) {
        final Player player = event.getPlayer();
        final UUID playerUniqueId = player.getUniqueId();

        if(event.getNewGameMode() == GameMode.SPECTATOR && PlayersManager.getLastDeathAt(playerUniqueId) != 0 && !PlayersManager.hasTimer(playerUniqueId)) {
            PlayersManager.setLife(playerUniqueId, PlayersManager.getLife(playerUniqueId) - 1);
            Normalcore.respawn(player);
        }
    }
}
