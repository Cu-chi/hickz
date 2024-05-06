package com.cuchi.hickz.misc;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class Skulls {
    private final ItemStack itemStack;

    private Skulls(String base64) {
        this.itemStack = createCustomSkull(base64);
    }

    public static Skulls TWITTER = new Skulls("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2M3NDVhMDZmNTM3YWVhODA1MDU1NTkxNDllYTE2YmQ0YTg0ZDQ0OTFmMTIyMjY4MThjMzg4MWMwOGU4NjBmYyJ9fX0=");
    public static Skulls DISCORD = new Skulls("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM1OWU1YzdiMDczOGI1NzlmM2I0NDRjMTNhNDdiZWQ0OTZiMzA4MzhiMmVlMmIxMjdjYzU5Y2Q3OThhZWU3NyJ9fX0=");
    public static Skulls YOUTUBE = new Skulls("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjQzNTNmZDBmODYzMTQzNTM4NzY1ODYwNzViOWJkZjBjNDg0YWFiMDMzMWI4NzJkZjExYmQ1NjRmY2IwMjllZCJ9fX0=");

    public ItemStack getItemStack() {
        return itemStack;
    }

    // https://mcheads.ru/en/tags
    private ItemStack createCustomSkull(String base64Str) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);

        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        profile.setProperty(new ProfileProperty("textures", base64Str));

        SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();
        itemMeta.setPlayerProfile(profile);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
