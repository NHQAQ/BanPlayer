package org.nanhai57501.banplayer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.sql.rowset.spi.SyncResolver;

public class Keys {
    public static final ItemStack HISTORIES = new ItemBuilder()
            .name("Histories")
            .enchant()
            .addLore("查看历史").build(Material.BOOK);
    public static final ItemStack PER_PAGE = new ItemBuilder()
            .name("上一页")
            .enchant()
            .build(Material.PAPER);
    public static final ItemStack NEXT_PAGE = new ItemBuilder()
            .name("下一页")
            .enchant()
            .build(Material.PAPER);

    public static final String INV_BAN_MENU = "BanMenu";
    public static final String INV_HISTORY = "History";
    public static final String Version = "1.0.0";
    public static final String Author = "NanHai";
    public static final String Cooperate = "Sweets0v0";

    public static String getUuidName(Player target) {return target.getUniqueId() + ":" + target.getName();}
}
