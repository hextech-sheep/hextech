package com.hextechsheep.hextech.gateway;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class Gateway extends JavaPlugin {
    private static final java.io.File DATA_STORE_DIR =
            new java.io.File(System.getProperty("user.home"), ".store/discord_data");

    private static FileDataStoreFactory DATA_STORE_FACTORY;
    private static final String SCOPE = "identify";
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private static final String TOKEN_SERVER_URL = "https://discordapp.com/api/oauth/token";
    private static final String AUTHORIZATION_SERVER_URL = "https://discordapp.com/api/oauth/authorize";

    private static Credential authorize() throws Exception {
        OAuth2ClientCredentials.errorIfNotSpecified();
        AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(
                BearerToken.authorizationHeaderAccessMethod(),
                HTTP_TRANSPORT,
                JSON_FACTORY,
                new GenericUrl(TOKEN_SERVER_URL),
                new ClientParametersAuthentication(
                        OAuth2ClientCredentials.API_KEY, OAuth2ClientCredentials.API_SECRET
                ),
                OAuth2ClientCredentials.API_KEY,
                AUTHORIZATION_SERVER_URL
            ).setScopes(Arrays.asList(SCOPE))
            .setDataStoreFactory(DATA_STORE_FACTORY).build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setHost(OAuth2ClientCredentials.DOMAIN)
                .setPort(OAuth2ClientCredentials.PORT)
                .build();

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private static void run(HttpRequestFactory requestFactory) throws IOException {
        GenericUrl url = new GenericUrl("https://discordapp.com/api/users/@me");
        HttpRequest request = requestFactory.buildGetRequest(url);
        Object user = request.execute().parseAs(Object.class);
        System.out.println(user);
    }

    public static void main(String[] args) {
        try {
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
            final Credential credential = authorize();
            HttpRequestFactory requestFactory =
                    HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                        @Override
                        public void initialize(HttpRequest httpRequest) throws IOException {
                            credential.initialize(httpRequest);
                            httpRequest.setParser(new JsonObjectParser(JSON_FACTORY));
                        }
                    });
            run(requestFactory);
            return;
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.exit(1);
    }


    private class LoginListener implements Listener {
        @EventHandler
        public void onPreLogin(final AsyncPlayerPreLoginEvent event) {
            final String name = event.getName();
            LOGGER.info(name + " is wanting to join!");
            Bukkit.broadcastMessage(name + " is wanting to join!");
//            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, "Go to <url> to authenticate with Discord.");
            event.allow();
        }
    }

    private final LoginListener listener = new LoginListener();
    private static final Logger LOGGER = LoggerFactory.getLogger(Gateway.class);

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(listener);
    }

    @Override
    public void onEnable() {
        LOGGER.info("Enabling Hextech Gateway");
        getServer().getPluginManager().registerEvents(listener, this);
        this.getCommand("leagueconnect").setExecutor(new CommandLeagueConnect());
        this.getCommand("leagueverify").setExecutor(new CommandLeagueVerify());
    }
}
