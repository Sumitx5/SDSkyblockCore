package org.sumit282698.sDSkyblockCore.menus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.sumit282698.sDSkyblockCore.SDSkyblockCore;
import org.sumit282698.sDSkyblockCore.api.PlayerSkills;

import java.util.ArrayList;
import java.util.List;

public class profilemenu implements Listener {

    private final Player player;
    private final Inventory inv;

    public profilemenu(Player player) {
        this.player = player;
        this.inv = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Your Skyblock Stats");
        setupItems();
    }

    private void setupItems() {
        inv.clear();

        PlayerSkills sPlayer = SDSkyblockCore.getSPlayer(player.getUniqueId());

        inv.setItem(11, createStatItem(Material.APPLE, "Health", sPlayer.getMaxHealth(), "Increases your max HP"));
        inv.setItem(13, createStatItem(Material.DIAMOND_SWORD, "Strength", sPlayer.getStrength(), "Increases your melee damage"));
        inv.setItem(15, createStatItem(Material.SHIELD, "Defense", sPlayer.getDefense(), "Reduces damage taken"));
        inv.setItem(22, createStatItem(Material.ENCHANTED_BOOK, "Intelligence", sPlayer.getMaxMana(), "Increases your mana pool"));
    }

    private ItemStack createStatItem(Material mat, String name, double value, String lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(ChatColor.YELLOW + name);

        List<String> loreList = new ArrayList<>();
        loreList.add(ChatColor.WHITE + lore);
        loreList.add(ChatColor.GREEN + "Current: " + ChatColor.GOLD + String.format("%.1f", value));
        // Removed "Click to add +10 points"
        meta.setLore(loreList);

        item.setItemMeta(meta);
        return item;
    }

    public void open() {
        setupItems(); // Make sure stats are fresh
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player clicker)) return;
        if (!event.getView().getTitle().equals(ChatColor.GREEN + "Your Skyblock Stats")) return;

        event.setCancelled(true); // prevent moving items
    }
}