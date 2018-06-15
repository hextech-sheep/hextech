package com.hextechsheep.hextech.greensheep;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CharSource;
import com.google.common.io.Resources;

public class SheepListener implements Listener {
    private static final Logger LOGGER = LoggerFactory.getLogger(SheepListener.class);
    private static final Random RANDOM = new Random();
    private static final String INSULTS_PATH = "/com/hextechsheep/hextech/greensheep/insults.txt";

    private static List<String> loadInsults() {
        final CharSource insults = Resources.asCharSource(SheepListener.class.getResource(INSULTS_PATH), Charset.forName("UTF-8"));
        try {
            return insults.readLines();
        } catch(final IOException e) {
            LOGGER.error("Failed to read insults file!", e);
            return Collections.emptyList();
        }
    }

    private final List<String> INSULTS = loadInsults();

    @EventHandler
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
        final LivingEntity entity = event.getEntity();

        if(entity instanceof Sheep) {
            final Sheep sheep = (Sheep)entity;

            if(sheep.getColor() != DyeColor.GREEN) {
                sheep.setColor(DyeColor.GREEN);
            }
        }
    }

    @EventHandler
    public void onSheepDyeWool(final SheepDyeWoolEvent event) {
        if(event.getColor() != DyeColor.GREEN) {
            event.setCancelled(true);

            if(event.getEntity().getColor() != DyeColor.GREEN) {
                event.getEntity().setColor(DyeColor.GREEN);
            }

            if(INSULTS != null && !INSULTS.isEmpty()) {
                final String insult = INSULTS.get(RANDOM.nextInt(INSULTS.size()));
                Bukkit.broadcastMessage(insult);
            }
        }
    }

    @EventHandler
    public void onSheepRegrowWool(final SheepRegrowWoolEvent event) {
        if(event.getEntity().getColor() != DyeColor.GREEN) {
            event.getEntity().setColor(DyeColor.GREEN);
        }
    }
}
