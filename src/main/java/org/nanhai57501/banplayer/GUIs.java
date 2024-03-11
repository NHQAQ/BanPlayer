package org.nanhai57501.banplayer;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GUIs {
    /*
     * ban menu
     * history menu
     */

    /**
     * 返回包含所有正在被ban玩家的列表(Ban Menu)
     * @param owner
     * @return
     */
    public List<Inventory> banMenus(Player owner) {
        List<Inventory> Menus = new ArrayList<>();
        Inventory inv = banMenu(owner);
        int index = 0;
        for (DataPlayer data: BanPlayer.data.dataPlayers) {
            if (data.getHistories().isEmpty()) continue;
            if (!data.isBan()) continue;
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            Player target = Bukkit.getPlayer(UUID.fromString(data.getTarget())); //在线玩家
            if (target == null) {
                continue;
            }
            meta.setOwningPlayer(target);
            meta.setDisplayName(target.getName());
            meta.setLore(Arrays.asList(
                    ChatColor.WHITE + "LeftClick to kill",
                    ChatColor.WHITE + "RightClick to kick"
            ));
            skull.setItemMeta(meta);
            skull.setAmount(1);
            inv.setItem(index++, skull);

            if (index % 45 == 0) {
                Menus.add(inv);
                inv = banMenu(owner);
            }
        }
        if (Menus.isEmpty()) {
            Menus.add(inv);
        }
        return Menus;
    }

    public List<Inventory> Histories(Player owner) {
        List<Inventory> histories = new ArrayList<>();
        Inventory inv = history(owner);
        int index = 0;
        for (DataPlayer data: BanPlayer.data.dataPlayers) {
            if (!(data.getHistories().isEmpty())) {
                ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                OfflinePlayer target = data.getPlayer();
                meta.setOwningPlayer(target);
                meta.setDisplayName(target.getName());
                meta.setLore(Arrays.asList(
                        ChatColor.RED + "Banned by: " + DataPlayer.toTime(data.getHistories().get(0).getBanTime())
                ));
                skull.setItemMeta(meta);
                skull.setAmount(1);
                inv.setItem(index++, skull);
            }

            if (index % 45 == 0) {
                histories.add(inv);
                inv = history(owner);
            }
        }
        if (histories.isEmpty()) {
            histories.add(inv);
        }
        return histories;
    }

    private Inventory banMenu(Player target) {
        Inventory inventory = Bukkit.createInventory(target, 9 * 6, "Ban Menu");
        inventory.setItem(53, Keys.HISTORIES);
        inventory.setItem(48, Keys.PER_PAGE);
        inventory.setItem(50, Keys.NEXT_PAGE);
        return inventory;
    }

    private Inventory history(Player target) {
        Inventory inventory = Bukkit.createInventory(target, 9 * 6, "History");
        inventory.setItem(48, Keys.PER_PAGE);
        inventory.setItem(50, Keys.NEXT_PAGE);
        return inventory;
    }

}
