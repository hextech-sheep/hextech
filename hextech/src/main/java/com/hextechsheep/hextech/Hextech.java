package com.hextechsheep.hextech;

import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hextech extends JavaPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(Hextech.class);

    @Override
    public void onDisable() {
        LOGGER.info("Goodbye, world!");
    }

    @Override
    public void onEnable() {
        LOGGER.info("Hello, world!");
    }
}
