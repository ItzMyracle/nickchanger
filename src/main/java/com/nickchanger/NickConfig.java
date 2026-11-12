package com.nickchanger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

/** Saved to .minecraft/config/nickchanger.json */
public class NickConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("nickchanger.json");

    /** The fake name. Supports colour codes with &, e.g. &6&lKing */
    public String nick = "";
    public boolean enabled = true;
    public boolean tabList = true;
    public boolean scoreboard = true;
    public boolean nametag = true;
    public boolean chat = true;

    public boolean active() {
        return enabled && nick != null && !nick.isBlank();
    }

    public static NickConfig load() {
        try {
            if (Files.exists(FILE)) {
                NickConfig c = GSON.fromJson(Files.readString(FILE), NickConfig.class);
                if (c != null) return c;
            }
        } catch (Exception e) {
            NickMod.LOGGER.warn("Could not read config, using defaults", e);
        }
        NickConfig c = new NickConfig();
        c.save();
        return c;
    }

    public void save() {
        try {
            Files.writeString(FILE, GSON.toJson(this));
        } catch (Exception e) {
            NickMod.LOGGER.warn("Could not save config", e);
        }
    }
}
