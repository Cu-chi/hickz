package com.cuchi.hickz.cases;

import com.cuchi.hickz.Hickz;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import static com.cuchi.hickz.misc.ItemCreator.createItem;

public class CaseLoader {
    private final Hashtable<String, Case> cases = new Hashtable<>();

    public CaseLoader(Hickz instance) {
        File file = new File(instance.getDataFolder(), "cases.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        List<String> caseNames = config.getStringList("cases");
        instance.getComponentLogger().info(Component.text("[CASE] Loading " +caseNames.size() + " cases.").color(TextColor.color(0,0,255)));

        caseNames.forEach(caseName -> {
            HashMap<ItemStack, BigDecimal> caseHashMap = new HashMap<>();
            instance.getComponentLogger().info(Component.text("[CASE] Loading " + caseName + " ("+"cases-"+caseName+".items"+")").color(TextColor.color(0,0,255)));
            List<String> items = config.getStringList("case-"+caseName+".items");
            String hexColor = config.getString("case-"+caseName+".color");

            if (items.isEmpty()) {
                instance.getComponentLogger().info(Component.text("[CASE] Can't load " + caseName + ": item list is empty").color(TextColor.color(255,0,0)));
                return;
            }

            items.forEach(itemName -> {
                String path = "case-" + caseName + "." + itemName;
                BigDecimal chance = new BigDecimal(Objects.requireNonNull(config.getString(path + ".chance")));
                Material material = Material.getMaterial(itemName);
                String newItemName = config.getString(path + ".itemName");

                List<String> itemLore = config.getStringList(path + ".itemDesc");
                List<Component> itemLoreComponents = new java.util.ArrayList<>(List.of());

                if (!itemLore.isEmpty())
                    itemLore.forEach(itemLoreRow -> itemLoreComponents.add(Component.text(itemLoreRow)));

                List<String> enchantNames = config.getStringList(path + ".enchants");
                HashMap<Enchantment, Integer> enchants = new HashMap<>();
                if (!enchantNames.isEmpty())
                    enchantNames.forEach(enchantName -> enchants.put(Registry.ENCHANTMENT.get(NamespacedKey.minecraft(enchantName)), config.getInt(path + "." + enchantName)));

                caseHashMap.put(createItem(material, !Objects.equals(newItemName, "") ? Component.text(Objects.requireNonNull(newItemName)) : null, config.getInt(path + ".amount"), itemLoreComponents, enchants), chance);
            });

            cases.put(caseName, new Case(Component.text(caseName).color(TextColor.fromHexString(hexColor != null ? "#"+hexColor : "#ffffff")), caseHashMap));
            instance.getComponentLogger().info(Component.text("[CASE] Successfully loaded " + caseName).color(TextColor.color(0,0,255)));
        });
        instance.getComponentLogger().info(Component.text("[CASE] Loaded all cases.").color(TextColor.color(0,0,255)));
    }

    public Case getCase(String caseName) {
        if (!cases.containsKey(caseName))
            throw new RuntimeException("[CASE] " + caseName + " doesn't exist.");

        return cases.get(caseName).cloneCase();
    }
}
