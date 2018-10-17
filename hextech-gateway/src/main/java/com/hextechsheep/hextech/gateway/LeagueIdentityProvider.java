package com.hextechsheep.hextech.gateway;

import com.merakianalytics.orianna.types.common.Region;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import com.merakianalytics.orianna.types.core.thirdpartycode.VerificationString;

public class LeagueIdentityProvider {    
    // Get Summoner name for user
    public String getSummonerName(String username) {
        // get from DB
        // otherwise
        return null;
    }
    
    // Make verification token to give to user
    public String beginVerification(String username) {
        // Generate a Token
        final String token = generateVerificationToken();
        
        // Save token to DB
        storeVerificationToken(username, token);
        
        // Return to user
        return token;
    }
    
    public boolean performVerification(String username, String summonerName, Region region) {
        // Get stored token from DB
        final String token = "helloworld";
        // Check API for 3rd-party-verification
        final Summoner summoner = Summoner.named(summonerName).withRegion(region).get();
        final VerificationString verification = summoner.getVerificationString();
        // Compare
        if (token.equals(verification.getString())) {
            // Store summoner info in DB
            return true;
        } else {
            return false;
        }
    }
    
    private String generateVerificationToken() {
        return java.util.UUID.randomUUID().toString();
    }
    
    private void storeVerificationToken(String username, String token) {
        // Store user + verification token in DB
    }
    
    

}
