package com.cuchi.hickz;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class Hickz extends JavaPlugin {
    private static Hickz instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();

        BukkitScheduler scheduler = getServer().getScheduler();

        int delay = getConfig().getInt("core.delay");

        if (delay > 0)
            scheduler.runTaskTimer(this, new BlockTask(), 0, delay * 20L);
        else
            System.out.println("core.delay in config.yml is less or equal to 0, must be greater than 0.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Hickz getInstance() {
        return instance;
    }
}
