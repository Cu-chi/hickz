package com.cuchi.hickz;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.function.Consumer;

public class BlockTask implements Consumer<BukkitTask> {
    private final Hickz hickzInstance;
    private final Random random = new Random();
    private final boolean sameBlock;
    private final List<Material> materials;

    public BlockTask() {
        hickzInstance = Hickz.getInstance();
        FileConfiguration config = hickzInstance.getConfig();
        List<String> blMaterials = config.getStringList("core.blacklisted-blocks");
        sameBlock = config.getBoolean("core.same");

        materials = Registry.MATERIAL.stream().filter(material -> material.isItem() && !blMaterials.contains(material.name())).toList();
    }

    @Override
    public void accept(BukkitTask task) {
        Collection<? extends Player> onlinePlayers = hickzInstance.getServer().getOnlinePlayers();
        Material generatedMaterial = null;

        if (sameBlock) generatedMaterial = getRandomMaterial();

        for (Player player : onlinePlayers) {
            if (player.isConnected() && !player.isDead()) {
                if (!sameBlock) generatedMaterial = getRandomMaterial();
                ItemStack item = new ItemStack(generatedMaterial);
                player.getInventory().addItem(item);

                player.sendMessage(Component.translatable("block-obtained", item.displayName().color(TextColor.color(255, 255, 0))));
            }
        }
    }

    private Material getRandomMaterial() {
        int index = random.nextInt(materials.size());
        return materials.get(index);
    }
}
