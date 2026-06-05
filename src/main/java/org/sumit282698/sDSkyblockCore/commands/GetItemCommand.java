package org.sumit282698.sDSkyblockCore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.sumit282698.sDSkyblockCore.SDSkyblockCore;

public class GetItemCommand implements CommandExecutor {

    private final SDSkyblockCore plugin;

    public GetItemCommand(SDSkyblockCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /sdget <item_id>");
            return true;
        }

        String itemId = args[0].toLowerCase();

        ItemStack item = plugin.getItemManager().getItem(itemId);
        if (item == null || item.getType().isAir()) {
            player.sendMessage("§cItem '" + itemId + "' not registered or failed to load!");
            return true;
        }

        String itemName = "§f" + itemId;
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            itemName = item.getItemMeta().getDisplayName();
        }

        player.getInventory().addItem(item);
        player.sendMessage("§a§lSKYBLOCK §7You received: " + itemName);

        return true;
    }
}