package com.cuchi.hickz;

import com.cuchi.hickz.cases.Case;
import com.cuchi.hickz.cases.CaseLoader;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Locale;
import java.util.ResourceBundle;

public final class Hickz extends JavaPlugin {
    private static Hickz instance;
    private static CaseLoader caseLoader;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
        saveResource("cases.yml", false);

        TranslationRegistry registry = TranslationRegistry.create(Key.key("hickz:locales"));
        ResourceBundle bundle = ResourceBundle.getBundle("locales.Bundle", Locale.US, UTF8ResourceBundleControl.get());
        registry.registerAll(Locale.US, bundle, true);
        bundle = ResourceBundle.getBundle("locales.Bundle", Locale.FRENCH, UTF8ResourceBundleControl.get());
        registry.registerAll(Locale.FRENCH, bundle, true);
        GlobalTranslator.translator().addSource(registry);

        BukkitScheduler scheduler = getServer().getScheduler();

        int delay = getConfig().getInt("core.delay");
        if (delay > 0)
            scheduler.runTaskTimer(this, new BlockTask(), 0, delay * 20L);
        else
            System.out.println("core.delay in config.yml is less or equal to 0, must be greater than 0.");

        caseLoader = new CaseLoader(this);

        PluginCommand command = getCommand("case");
        if (command != null) command.setExecutor(new CommandHandler());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Hickz getInstance() {
        return instance;
    }

    public static Case registerCase(String caseName) {
        Case caseRegister = caseLoader.getCase(caseName);
        instance.getServer().getPluginManager().registerEvents(caseRegister, instance);
        return caseRegister;
    }
}
