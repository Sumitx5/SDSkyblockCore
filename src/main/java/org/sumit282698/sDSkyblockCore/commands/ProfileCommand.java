package org.sumit282698.sDSkyblockCore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.sumit282698.sDSkyblockCore.SDSkyblockCore;
import org.sumit282698.sDSkyblockCore.menus.ProfileMenu;

public class ProfileCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        ProfileMenu menu = new ProfileMenu(player);
        SDSkyblockCore.getInstance().getServer().getPluginManager().registerEvents(menu, SDSkyblockCore.getInstance());
        menu.open();

        return true;
    }
}