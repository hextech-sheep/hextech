package com.hextechsheep.hextech.gateway;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.merakianalytics.orianna.types.common.Region;

import java.util.UUID;

public class CommandLeagueVerify implements CommandExecutor {
    final LeagueIdentityProvider leagueIdentityProvider = new LeagueIdentityProvider();
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player");
        } else {
            Player player = (Player) sender;
            UUID playerId = player.getUniqueId();
            if (playerId == null) {
                return false;
            }
            boolean verified = leagueIdentityProvider.performVerification(playerId, "WxWatch", Region.NORTH_AMERICA);
            if (verified) {
                player.sendMessage("Successfully verified!");
            } else {
                player.sendMessage("Verification failed, please try again");
            }
        }
        
        return true;
    }
}
