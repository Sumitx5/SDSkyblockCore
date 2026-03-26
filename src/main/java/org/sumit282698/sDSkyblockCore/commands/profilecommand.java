package org.sumit282698.sDSkyblockCore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sumit282698.sDSkyblockCore.SDSkyblockCore;
import org.sumit282698.sDSkyblockCore.menus.profilemenu;

public class profilecommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        profilemenu menu = new profilemenu(player);
        SDSkyblockCore.getInstance().getServer().getPluginManager().registerEvents(menu, SDSkyblockCore.getInstance());
        menu.open();

        return true;
    }
}