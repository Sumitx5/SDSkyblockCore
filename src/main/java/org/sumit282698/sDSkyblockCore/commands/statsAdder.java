package org.sumit282698.sDSkyblockCore.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sumit282698.sDSkyblockCore.SDSkyblockCore;
import org.sumit282698.sDSkyblockCore.api.PlayerSkills;

public class statsAdder implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 1. Permissions Check
        if (!sender.hasPermission("skyblock.admin")) {
            sender.sendMessage("§cYou don't have permission, Buddy!");
            return true;
        }

        // 2. Syntax: /setstat <player> <stat> <value>
        if (args.length < 3) {
            sender.sendMessage("§eUsage: /sdskills <player> <health|defense|strength|intelligence> <value>");
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

        // 3. Update the Stat
        switch (stat) {
            case "health":
                sPlayer.setMaxHealth(sPlayer.getMaxHealth() + value);
            case "defense":
                sPlayer.setMaxHealth(sPlayer.getMaxHealth() + value);
            case "strength":
                sPlayer.setMaxHealth(sPlayer.getMaxHealth() + value);
            case "intelligence":
                sPlayer.setMaxHealth(sPlayer.getMaxHealth() + value);
            default:
                sender.sendMessage("§cInvalid stat! Use health, defense, strength, or intelligence.");
        }

        sender.sendMessage("§aSet " + target.getName() + "'s " + stat + " to " + value + "!");

        return true;
    }
}
