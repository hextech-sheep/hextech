package com.hextechsheep.hextech;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CharSource;
import com.google.common.io.Resources;

public class Hextech extends JavaPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(Hextech.class);

    private static List<JavaPlugin> getComponents() {
        final CharSource components = Resources.asCharSource(Resources.getResource("com/hextechsheep/hextech/components.txt"), Charset.forName("UTF-8"));

        List<String> classes;
        try {
            classes = components.readLines();
        } catch(final IOException e) {
            LOGGER.error("Failed to read components file!", e);
            return Collections.emptyList();
        }

        return classes.stream().map((final String className) -> {
            Class<?> clazz;
            try {
                clazz = Class.forName(className);
            } catch(final ClassNotFoundException e) {
                LOGGER.error("Couldn't load component " + className + "!", e);
                return null;
            }

            Object component;
            try {
                component = clazz.newInstance();
            } catch(InstantiationException | IllegalAccessException e) {
                LOGGER.error("Couldn't instantiate component " + className + "!", e);
                return null;
            }

            if(!(component instanceof JavaPlugin)) {
                return null;
            }
            return (JavaPlugin)component;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private final List<JavaPlugin> COMPONENTS = getComponents();

    @Override
    public void onDisable() {
        for(final JavaPlugin component : COMPONENTS) {
            Bukkit.getPluginManager().disablePlugin(component);
        }
    }

    @Override
    public void onEnable() {
        for(final JavaPlugin component : COMPONENTS) {
            Bukkit.getPluginManager().enablePlugin(component);
        }
    }
}
