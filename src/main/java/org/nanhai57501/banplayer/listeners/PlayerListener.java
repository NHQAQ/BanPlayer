package org.nanhai57501.banplayer.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.nanhai57501.banplayer.BanPlayer;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player target = event.getPlayer();
        BanPlayer.data.findPlayerData(target.getUniqueId().toString());
    }
}
