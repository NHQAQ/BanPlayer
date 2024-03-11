package org.nanhai57501.banplayer.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.nanhai57501.banplayer.BanPlayer;
import org.nanhai57501.banplayer.GUIs;
import org.nanhai57501.banplayer.Keys;
import org.nanhai57501.banplayer.listeners.GuiListener;

import java.util.List;

public class GuestCommand implements CommandExecutor {
    // /bm or /banMenu
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!commandSender.hasPermission("banplayer.command.Guest")) return false;
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command!");
            return true;
        }
        Player player = (Player) commandSender;

        List<Inventory> inventories = openMenu(player);
        GuiListener.inv_open_now = inventories;

        return true;
    }

    private List<Inventory> openMenu(Player player) {
        List<Inventory> banMenus = BanPlayer.guis.banMenus(player);
        player.openInventory(banMenus.get(0));
        player.setMetadata(Keys.INV_BAN_MENU, new FixedMetadataValue(BanPlayer.getInstance(), Keys.INV_BAN_MENU));
        return banMenus;
    }


}
