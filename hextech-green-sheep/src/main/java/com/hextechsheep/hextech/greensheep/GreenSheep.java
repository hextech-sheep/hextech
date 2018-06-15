package com.hextechsheep.hextech.greensheep;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CharSource;
import com.google.common.io.Resources;

public class GreenSheep extends JavaPlugin {
    private class SheepListener implements Listener {
        @EventHandler
        public void onEntitySpawn(final EntitySpawnEvent event) {
            final Entity entity = event.getEntity();

            if(entity instanceof Sheep) {
                final Sheep sheep = (Sheep)entity;
                if(sheep.getColor() != GREEN) {
                    sheep.setColor(GREEN);
                }
            }
        }

        @EventHandler
        public void onSheepDyeWool(final SheepDyeWoolEvent event) {
            if(event.getColor() != GreenSheep.GREEN) {
                if(insults != null && !insults.isEmpty()) {
                    final String insult = insults.get(ThreadLocalRandom.current().nextInt(insults.size()));
                    Bukkit.broadcastMessage(insult);
                }
                
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        event.getEntity().setHealth(0.0);
                    }
                }.runTaskLater(GreenSheep.this, ThreadLocalRandom.current().nextLong(MINIMUM_DEATH_TICKS, MAXIMUM_DEATH_TICKS + 1L));
            }
        }

        @EventHandler
        public void onSheepRegrowWool(final SheepRegrowWoolEvent event) {
            if(event.getEntity().getColor() != GREEN) {
                event.getEntity().setColor(GREEN);
            }
        }
    }

    private static final DyeColor GREEN = DyeColor.LIME;
    private static final String INSULTS_PATH = "/com/hextechsheep/hextech/greensheep/insults.txt";
    private static final Logger LOGGER = LoggerFactory.getLogger(GreenSheep.class);
    private static final long MINIMUM_DEATH_TICKS = 200L; // Roughly 10 seconds
    private static final long MAXIMUM_DEATH_TICKS = 1200L; // Roughly 1 minute

    private static List<String> loadInsults() {
        final CharSource insults = Resources.asCharSource(SheepListener.class.getResource(INSULTS_PATH), Charset.forName("UTF-8"));
        try {
            return insults.readLines();
        } catch(final IOException e) {
            LOGGER.error("Failed to read insults file!", e);
            return Collections.emptyList();
        }
    }

    private final List<String> insults = loadInsults();
    private final SheepListener listener = new SheepListener();

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(listener);

        LOGGER.info("Randomly redyeing the sheep...");
        final DyeColor[] colors = DyeColor.values();
        for(final World world : Bukkit.getWorlds()) {
            for(final Sheep sheep : world.getEntitiesByClass(Sheep.class)) {
                final DyeColor color = colors[ThreadLocalRandom.current().nextInt(colors.length)];
                if(sheep.getColor() != color) {
                    sheep.setColor(color);
                }
            }
        }
        LOGGER.info("The sheep (probably) aren't all green anymore. Look at what you've done.");
    }

    @Override
    public void onEnable() {
        LOGGER.info("Dyeing all the sheep green...");
        for(final World world : Bukkit.getWorlds()) {
            for(final Sheep sheep : world.getEntitiesByClass(Sheep.class)) {
                if(sheep.getColor() != GREEN) {
                    sheep.setColor(GREEN);
                }
            }
        }
        LOGGER.info("All the sheep are now green.");

        getServer().getPluginManager().registerEvents(listener, this);
    }
}
