package org.sumit282698.sDSkyblockCore.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.sumit282698.sDSkyblockCore.SDSkyblockCore;
import org.sumit282698.sDSkyblockCore.api.PlayerSkills;

public class StatAdder implements CommandExecutor {
    private final SDSkyblockCore plugin;

    public StatAdder(SDSkyblockCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {

        //Permission check
        if (!sender.hasPermission("sdskyblock.admin")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }

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
        if (sPlayer == null) {
            sender.sendMessage("§cFailed to load player core data!");
            return true;
        }

        String stat = args[1].toLowerCase();
        double value;

        try {
            value = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cValue must be a number!");
            return true;
        }

        switch (stat) {
            case "health":
                sPlayer.setBaseMaxHealth(value);
                sPlayer.setCurrentHealth(sPlayer.getMaxHealth());
                break;

            case "defense":
                sPlayer.setBaseDefense(value);
                break;

            case "strength":
                sPlayer.setBaseStrength(value);
                break;

            case "intelligence":
            case "mana":
                sPlayer.setBaseMaxMana(value);
                sPlayer.setCurrentMana(sPlayer.getMaxMana());
                break;

            case "crit_chance":
            case "cc":
                sPlayer.setBaseCritChance(value);
                break;

            case "crit_damage":
            case "cd":
                sPlayer.setBaseCritDamage(value);
                break;

            default:
                sender.sendMessage("§cInvalid stat! Use: health, defense, strength, intelligence, cc, cd");
                return true;
        }

        sender.sendMessage("§aSuccessfully updated " + target.getName() + "'s base " + stat + " to " + value + "!");
        target.sendMessage("§aYour base " + stat + " was updated to " + value + " by an administrator.");
        return true;
    }
}