package com.hextechsheep.hextech.gateway;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.merakianalytics.orianna.types.common.Region;

public class CommandLeagueVerify implements CommandExecutor {
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
            boolean verified = leagueIdentityProvider.performVerification(username, "WxWatch", Region.NORTH_AMERICA);
            if (verified) {
                player.sendMessage("Successfully verified!");
            } else {
                player.sendMessage("Verification failed, please try again");
            }
        }
        
        return true;
    }
}
