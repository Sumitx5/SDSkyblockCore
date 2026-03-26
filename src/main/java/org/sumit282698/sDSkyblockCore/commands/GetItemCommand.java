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
        // 1. Check if the sender is a player (not the console)
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        // 2. Check if they provided an ID (e.g., /sbget aote)
        if (args.length == 0) {
            player.sendMessage("§cUsage: /sdget <item_id>");
            return true;
        }

        String itemId = args[0];

        // 3. Grab the item from our ItemManager
        ItemStack item = SDSkyblockCore.getInstance().getItemManager().getItem(itemId);

        if (item != null) {
            player.getInventory().addItem(item);
            player.sendMessage("§a§lSKYBLOCK §7You received: " + item.getItemMeta().getDisplayName());
        } else {
            player.sendMessage("§cItem '" + itemId + "' not found in the items folder!");
        }

        return true;
    }
}
