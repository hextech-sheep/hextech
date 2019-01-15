package com.hextechsheep.hextech.gateway;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandLeagueConnect implements CommandExecutor {
    final LeagueIdentityProvider leagueIdentityProvider = new LeagueIdentityProvider();
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player");
        } else {
            Player player = (Player) sender;
            UUID playerId = player.getUniqueId();
            if (playerId == null) {
                sender.sendMessage("Something went wrong: Player not found :/");
                return false;
            }
            String token = leagueIdentityProvider.createVerificationToken(playerId);
            if (token == null) {
                sender.sendMessage("Something went wrong: Verification token couldn't be generated :/");
                return false;
            }
            
            player.sendMessage("Log into your League of Legends client, go to Settings -> Verification, and enter the following code: " + token);
            player.sendMessage("Once you've done that, use the /leagueverify command to finish connecting your Summoner.");
        }
        
        return true;
    }

}
