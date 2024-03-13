package org.nanhai57501.banplayer.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.nanhai57501.banplayer.BanPlayer;
import org.nanhai57501.banplayer.GUIs;
import org.nanhai57501.banplayer.Keys;

import java.util.List;
import java.util.Objects;

public class GuiListener implements Listener {
    public static List<Inventory> inv_open_now;

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player owner = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        if (owner.hasMetadata(Keys.INV_BAN_MENU) || owner.hasMetadata(Keys.INV_HISTORY)) {
            event.setCancelled(true);
            //界面物品点击
            switch (item.getItemMeta().getDisplayName()) {
                case "上一页":
                    int index = inv_open_now.indexOf(event.getInventory());
                    if (index > 0) {
                        owner.closeInventory();
                        owner.openInventory(inv_open_now.get(index - 1));
                    }
                    break;
                case "下一页":
                    int index1 = inv_open_now.indexOf(event.getInventory());
                    if (index1 < inv_open_now.size() - 1) {
                        owner.closeInventory();
                        owner.openInventory(inv_open_now.get(index1 + 1));
                    }
                    break;
                case "Histories":
                    owner.closeInventory();
                    setInv_history(owner);
                    owner.removeMetadata(Keys.INV_BAN_MENU, BanPlayer.getInstance());
                    owner.openInventory(inv_open_now.get(0));
                    owner.setMetadata(Keys.INV_HISTORY, new FixedMetadataValue(BanPlayer.getInstance(), Keys.INV_HISTORY));
                    break;
            }

            //对头颅的操作喵
            if (item.getType() == Material.PLAYER_HEAD && owner.hasMetadata(Keys.INV_BAN_MENU)) {
                //拿到头颅的主人(如果点击的是头颅)
                Player target = getTargetPlayer(event);
                if (event.getClick().isLeftClick()) {
                    Objects.requireNonNull(target).setHealth(0);
                    owner.sendMessage("你kill了" + target.getName() + "喵");
                } else if (event.getClick().isRightClick()) {
                    Objects.requireNonNull(target).kickPlayer("你被踢出服务器了喵");
                    owner.sendMessage("你踢出了" + target.getName() + "喵");
                }
            }
        }

    }

    private void setInv_history(Player target) {
        GuiListener.inv_open_now = BanPlayer.guis.Histories(target); //将正在打开的容器设置为Histories
    }

    private Player getTargetPlayer(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (Objects.requireNonNull(item).getType() == Material.PLAYER_HEAD) {
            SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
            Player target = Objects.requireNonNull(skullMeta.getOwningPlayer()).getPlayer();
            return target;
        }
        return null;
    }

    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (player.hasMetadata(Keys.INV_BAN_MENU)) {
            player.removeMetadata(Keys.INV_BAN_MENU, BanPlayer.getInstance());
        }
        if (player.hasMetadata(Keys.INV_HISTORY)) {
            player.removeMetadata(Keys.INV_HISTORY, BanPlayer.getInstance());
        }
    }
}
