package org.sumit282698.sDSkyblockCore.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.sumit282698.sDSkyblockCore.SDSkyblockCore;
import org.sumit282698.sDSkyblockCore.api.PlayerSkills;

public class statsAdder implements CommandExecutor {
    private final SDSkyblockCore plugin;

    public statsAdder(SDSkyblockCore plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Permission check
        if (!sender.hasPermission("sdskyblock.admin")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }

        // Correct usage
        if (args.length < 3) {
            sender.sendMessage("§eUsage: /sdskills <player> <stat> <value>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return true;
        }

        PlayerSkills sPlayer = SDSkyblockCore.getSPlayer(target.getUniqueId());
        String stat = args[1].toLowerCase();
        double value;

        try {
            value = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cValue must be a number!");
            return true;
        }

        // Apply stat
        switch (stat) {

            case "health":
                sPlayer.setMaxHealth(value);
                break;

            case "defense":
                sPlayer.setDefense(value);
                break;

            case "strength":
                sPlayer.setStrength(value);
                break;

            case "intelligence":
                sPlayer.setMaxMana(value);
                break;

            default:
                sender.sendMessage("§cInvalid stat! Use: health, defense, strength, intelligence");
                return true;
        }

        sender.sendMessage("§aSet " + target.getName() + "'s " + stat + " to " + value + "!");
        return true;
    }
}