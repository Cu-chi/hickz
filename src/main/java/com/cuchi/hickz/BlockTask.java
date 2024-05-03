package com.cuchi.hickz;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class BlockTask implements Consumer<BukkitTask> {
    private final int materialsLength = Material.values().length;
    private final Hickz hickzInstance;
    private final List<String> blMaterials;
    private final Random random = new Random();
    private final FileConfiguration config;
    private final boolean sameBlock;

    public BlockTask() {
        hickzInstance = Hickz.getInstance();
        config = hickzInstance.getConfig();
        blMaterials = config.getStringList("core.blacklisted-blocks");
        sameBlock = config.getBoolean("core.same");
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
            }
        }
    }

    private Material getRandomMaterial() {
        int index = random.nextInt(materialsLength);

        for (Material material : Material.values()) {
            if (index <= 0 && !blMaterials.contains(material.name()))
                return material;
            index--;
        }

        // in case of not getting any item, then we recursively call the func
        return getRandomMaterial();
    }
}
