package com.hextechsheep.hextech.gateway;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.merakianalytics.orianna.types.common.Region;

import java.util.Arrays;
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

            if (args.length == 0 && args.length == 1) {
                return false;
            }

            String summonerName = String.join(" ", Arrays.copyOfRange(args, 0, args.length - 1));
            if (summonerName == null) {
                return false;
            }

            String regionString = args[args.length - 1];
            Region region = getRegion(regionString);
            if (region == null) {
                return false;
            }

            boolean verified = leagueIdentityProvider.verifyToken(playerId, summonerName, region);
            if (verified) {
                player.sendMessage("Successfully verified!");
            } else {
                player.sendMessage("Verification failed, please try again");
            }
        }
        
        return true;
    }

    private Region getRegion(String shortname) {
        switch (shortname.toLowerCase()) {
            case "br":
                return Region.BRAZIL;
            case "eune":
                return Region.EUROPE_NORTH_EAST;
            case "euw":
                return Region.EUROPE_WEST;
            case "jp":
                return Region.JAPAN;
            case "kr":
                return Region.KOREA;
            case "lan":
                return Region.LATIN_AMERICA_NORTH;
            case "las":
                return Region.LATIN_AMERICA_SOUTH;
            case "na":
                return Region.NORTH_AMERICA;
            case "oce":
                return Region.OCEANIA;
            case "ru":
                return Region.RUSSIA;
            case "tr":
                return Region.TURKEY;
            default:
                return null;
        }
    }
}
