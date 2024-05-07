package com.cuchi.hickz.cases;

import com.cuchi.hickz.Hickz;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.util.*;

public class Case implements Listener {
    private final HashMap<ItemStack, BigDecimal> obtainableItems;
    private final List<ItemStack> itemStackList;
    private final Inventory caseInventory;
    private final Random random = new Random();
    private boolean closeInventory = false;
    private final Component caseTitle;

    public Case(Component title, HashMap<ItemStack, BigDecimal> caseHashMap) {
        caseTitle = title;
        caseInventory = Bukkit.createInventory(null, 27, caseTitle);

        ItemStack whiteGlass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemStack redGlass = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        for (int i = 0; i < 9; i++)
            if (i % 4 == 0) caseInventory.setItem(i, redGlass);
            else caseInventory.setItem(i, whiteGlass);

        int itemsIndex = 0;
        while (itemsIndex < 9) {
            for (ItemStack item : caseHashMap.keySet()) {
                caseInventory.setItem(itemsIndex + 9, item);
                if (itemsIndex++ >= 9) break;
            }
        }

        for (int i = 0; i < 9; i++)
            if (i % 4 == 0) caseInventory.setItem(i + 18, redGlass);
            else caseInventory.setItem(i + 18, whiteGlass);

        BigDecimal totalChance = new BigDecimal("0.0");
        for (BigDecimal chance : caseHashMap.values()) totalChance = totalChance.add(chance);
        if (totalChance.compareTo(new BigDecimal("1.0")) != 0) throw new RuntimeException("Case has to have a total chance of 1.0f. Had " + totalChance+"f");

        obtainableItems = caseHashMap;
        itemStackList = obtainableItems.keySet().stream().toList();
    }

    // You can open the inventory with this
    private void openInventory(HumanEntity ent) {
        ent.openInventory(caseInventory);
    }

    public void openCase(HumanEntity ent) {
        BigDecimal generated = BigDecimal.valueOf(random.nextFloat());

        ItemStack wonItem = null;
        for (Map.Entry<ItemStack, BigDecimal> map : obtainableItems.entrySet()) {
            int result = generated.compareTo(map.getValue());
            if (result < 0) {
                wonItem = map.getKey();
                break;
            }

            generated = generated.subtract(map.getValue()); // we need to substract since we are continuing the loop
        }

        if (wonItem == null) {
            ent.sendMessage(Component.text("Was " + generated.toPlainString()));
            throw new RuntimeException("wonItem is null");
        }

        openInventory(ent);
        animate(ent, wonItem);
    }

    private void animate(HumanEntity ent, ItemStack wonItem) {
        Hickz instance = Hickz.getInstance();

        new BukkitRunnable() {
            int duration = instance.getConfig().getInt("core.case-duration") * 10;
            boolean needCenter = false;
            int wonAtIndex = 0;
            final World entWorld = ent.getWorld();
            @Override
            public void run() {
                entWorld.playSound(ent, Sound.BLOCK_NOTE_BLOCK_BELL, 10, 1);
                duration--;

                if (needCenter) wonAtIndex++;

                for (int i = 9+7; i > 8; i--) {
                    ItemStack curIndexItemStack = caseInventory.getItem(i);
                    caseInventory.setItem(i+1, curIndexItemStack);
                }

                if (needCenter && wonAtIndex == 9)
                    caseInventory.setItem(9, wonItem);
                else {
                    int index = random.nextInt(itemStackList.size());
                    caseInventory.setItem(9, itemStackList.get(index));
                }

                if ((duration == 0 && !needCenter && wonAtIndex == 0) || (closeInventory && duration > 0)) {
                    duration = 0;
                    closeInventory = true;
                    needCenter = true;
                    wonAtIndex = 8;
                }

                if (needCenter && wonAtIndex == 13) {
                    entWorld.playSound(ent, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 10, 1);
                    ent.getInventory().addItem(wonItem);
                    instance.getServer().sendMessage(Component.translatable("case-reward", ent.name(), wonItem.displayName(), caseTitle));
                    this.cancel();
                }
            }
        }.runTaskTimer(instance, 0L, 2L);
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(caseInventory))
            e.setCancelled(true);
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(InventoryDragEvent e) {
        if (e.getInventory().equals(caseInventory))
            e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().equals(caseInventory))
            closeInventory = true;
    }

    public Case cloneCase()  {
        return new Case(this.caseTitle, new HashMap<>(this.obtainableItems));
    }
}
