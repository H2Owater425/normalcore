package kr.dhmo.normalcore.listeners;

import kr.dhmo.normalcore.Normalcore;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

final public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Normalcore.setServerTickFrozen(Normalcore.getOnlinePlayerCount() == 1);
    }
}
