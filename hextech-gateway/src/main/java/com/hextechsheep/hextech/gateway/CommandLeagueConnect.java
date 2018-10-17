package com.hextechsheep.hextech.gateway;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLeagueConnect implements CommandExecutor {
    final LeagueIdentityProvider leagueIdentityProvider = new LeagueIdentityProvider();
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player");
        } else {
            Player player = (Player) sender;
            String username = player.getName();
            if (username == null) {
                return false;
            }
            String token = leagueIdentityProvider.beginVerification(username);
            if (token == null) {
                return false;
            }
            
            player.sendMessage("Please go into League and use this verification token: " + token);
        }
        
        return true;
    }

}
