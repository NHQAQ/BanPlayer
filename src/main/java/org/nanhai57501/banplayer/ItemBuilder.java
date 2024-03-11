package org.nanhai57501.banplayer;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    private Material material;
    private int count = 1;
    private boolean enchanted;
    private List<String> lore = new ArrayList<>();
    private String name;

    public ItemBuilder name(String name) {
        this.name = name;
        return this;
    }
    public ItemBuilder enchant() {
        enchanted = true;
        return this;
    }
    public ItemBuilder setCount(int count) {
        this.count = count;
        return this;
    }
    public ItemBuilder addLore(String lore) {
        this.lore.add(lore);
        return this;
    }
    public ItemBuilder removeLore(int index) {
        this.lore.remove(index);
        return this;
    }
    public ItemStack build(Material material) {
        ItemStack result = new ItemStack(material);
        ItemMeta itemMeta = result.getItemMeta();
        if (enchanted) {
            itemMeta.addEnchant(Enchantment.CHANNELING, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemMeta.setLore(lore);
        itemMeta.setDisplayName(name);
        result.setItemMeta(itemMeta);
        result.setAmount(count);
        return result;
    }
}
