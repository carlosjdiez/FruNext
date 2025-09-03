package com.svalero.frunext.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class ResourceManager {
    public static AssetManager assets = new AssetManager();

    // === NUEVO: opciones configurables ===
    public static boolean musicEnabled = true;
    public static boolean sfxEnabled = true;
    // =====================================

    public static void loadAllResources() {
        // Sonidos que SÍ tienes
        assets.load("sounds/coin.wav", Sound.class);
        assets.load("sounds/explosion.wav", Sound.class); // al pisar enemigo
        assets.load("sounds/jump.wav", Sound.class);
        assets.load("sounds/player_down.wav", Sound.class);

        assets.load("sounds/level_clear.mp3", Music.class);
        assets.load("sounds/levels.mp3", Music.class); // música fondo

        // Atlases
        assets.load("items/items.pack", TextureAtlas.class);
        assets.load("characters/characters.pack", TextureAtlas.class);
    }

    public static boolean update() { return assets.update(); }
    public static void finishLoading() { assets.finishLoading(); }

    public static TextureAtlas getAtlas(String path) { return assets.get(path, TextureAtlas.class); }
    public static Sound getSound(String path) { return assets.get(path, Sound.class); }
    public static Music getMusic(String path) { return assets.get(path, Music.class); }

    public static void dispose() { assets.dispose(); }
}
