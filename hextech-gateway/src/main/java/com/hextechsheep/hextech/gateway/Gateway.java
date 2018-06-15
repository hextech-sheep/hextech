package com.hextechsheep.hextech.gateway;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gateway extends JavaPlugin {
    private class LoginListener implements Listener{
        @EventHandler
        public void onPreLogin(final AsyncPlayerPreLoginEvent event) {
            final String name = event.getName();
            LOGGER.info(name + " is wanting to join!");
            Bukkit.broadcastMessage(name + " is wanting to join!");
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
    }
}
