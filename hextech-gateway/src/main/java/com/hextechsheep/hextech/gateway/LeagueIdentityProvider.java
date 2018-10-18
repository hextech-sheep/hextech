package com.hextechsheep.hextech.gateway;

import com.merakianalytics.orianna.types.common.Region;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import com.merakianalytics.orianna.types.core.thirdpartycode.VerificationString;

import java.util.UUID;

public class LeagueIdentityProvider {
    private final IdentityManager identityManager;

    public LeagueIdentityProvider() {
        identityManager = new IdentityManager();
    }

    // Make verification token to give to user
    public String beginVerification(UUID minecraftId) {
        final String token = generateVerificationToken();
        storeVerificationToken(minecraftId, token);
        
        return token;
    }
    
    public boolean performVerification(UUID minecraftId, String summonerName, Region region) {
        final String token = getVerificationToken(minecraftId);
        final Summoner summoner = Summoner.named(summonerName).withRegion(region).get();
        final VerificationString verification = summoner.getVerificationString();

        if (token.equals(verification.getString())) {
            storeSummoner(minecraftId, summoner);
            storeVerificationToken(minecraftId, "");

            return true;
        } else {
            return false;
        }
    }
    
    private String generateVerificationToken() {
        return java.util.UUID.randomUUID().toString();
    }

    private void storeSummoner(UUID minecraftId, Summoner summoner) {
        final IdentityManager.Identity identity = identityManager.get(minecraftId.toString());
        identity.setSummoner(summoner);
    }
    
    private void storeVerificationToken(UUID minecraftId, String token) {
        // Store user + verification token in DB
        final IdentityManager.Identity identity = identityManager.get(minecraftId.toString());
        identity.setLeagueVerifyToken(token);
    }

    private String getVerificationToken(UUID minecraftId) {
        final IdentityManager.Identity identity = identityManager.get(minecraftId.toString());
        return identity.getLeagueVerifyToken();
    }
}
