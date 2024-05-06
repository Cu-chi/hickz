package com.cuchi.hickz.misc;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public final class ItemCreator {
    public static ItemStack createItem(Material material, Component name, int amount, List<Component> lore, HashMap<Enchantment, Integer> enchants) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta itemMeta = item.getItemMeta();

        if (name != null) itemMeta.displayName(name);

        if (!lore.isEmpty()) itemMeta.lore(lore);

        if (!enchants.isEmpty())
            for (Enchantment enchantment : enchants.keySet())
                itemMeta.addEnchant(enchantment, enchants.get(enchantment), true);

        item.setItemMeta(itemMeta);

        return item;
    }
}
