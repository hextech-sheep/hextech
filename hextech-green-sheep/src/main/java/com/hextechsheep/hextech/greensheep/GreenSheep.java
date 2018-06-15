package com.hextechsheep.hextech.greensheep;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.entity.Sheep;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreenSheep extends JavaPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(GreenSheep.class);
    private static final Random RANDOM = new Random();
    private final SheepListener listener = new SheepListener();

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(listener);

        LOGGER.info("Randomly redyeing the sheep...");
        final DyeColor[] colors = DyeColor.values();
        for(final World world : Bukkit.getWorlds()) {
            for(final Sheep sheep : world.getEntitiesByClass(Sheep.class)) {
                sheep.setColor(colors[RANDOM.nextInt(colors.length)]);
            }
        }
        LOGGER.info("The sheep (probably) aren't all green anymore. Look at what you've done.");
    }

    @Override
    public void onEnable() {
        LOGGER.info("Dyeing all the sheep green...");
        for(final World world : Bukkit.getWorlds()) {
            for(final Sheep sheep : world.getEntitiesByClass(Sheep.class)) {
                if(sheep.getColor() != DyeColor.GREEN) {
                    sheep.setColor(DyeColor.GREEN);
                }
            }
        }
        LOGGER.info("All the sheep are now green.");

        getServer().getPluginManager().registerEvents(listener, this);
    }
}
