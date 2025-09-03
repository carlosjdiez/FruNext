package com.svalero.frunext.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.svalero.frunext.characters.Enemy;
import com.svalero.frunext.characters.Platform;
import com.svalero.frunext.characters.Platform.Direction;

public class LevelManager {
    public static final String LEVEL_DIR = "levels";
    public static final String LEVEL_PREFIX = "level";
    public static final String LEVEL_EXTENSION = ".tmx";

    public Array<Enemy> enemies = new Array<>();
    public Array<Platform> platforms = new Array<>();
    public Array<Rectangle> fruits = new Array<>();

    public TiledMap map;

    // Parámetros de nivel
    public int currentLevel;
    public int currentLives;
    public int totalCoins;
    public int currentCoins;


    public boolean highLevel;

    public LevelManager() {
        currentLevel = 1;
        currentLives = 3;
        currentCoins = 0;
        totalCoins   = 0;
        highLevel    = false;
    }

    public void passCurrentLevel() { currentLevel++; }

    public void finishCurrentLevel() {
        totalCoins += currentCoins;
        currentCoins = 0;
        clearCharactersCurrentLevel();

    }

    public void restartCurrentLevel() {
        currentCoins = 0;
        currentLives--;
        clearCharactersCurrentLevel();
    }

    public String getCurrentLevelName() { return LEVEL_PREFIX + currentLevel; }
    public String getCurrentLevelPath() { return LEVEL_DIR + "/" + getCurrentLevelName() + LEVEL_EXTENSION; }

    public void loadCurrentMap() {
        TiledMapManager.setLevelManager(this);
        map = new TmxMapLoader().load(getCurrentLevelPath());
        TiledMapManager.collisionLayer = (TiledMapTileLayer) map.getLayers().get("terrain");
        TiledMapManager.objectLayer    = map.getLayers().get("objects");

        loadAnimateTiles();
        loadEnemies();
        loadPlatforms();
        loadFruits();
    }

    private void loadAnimateTiles() {
        // Monedas
        TiledMapManager.animateTiles("coin", 6, 0.14f);
        // Mar
        TiledMapManager.animateTiles("sea", 2, 0.60f);
    }

    private void loadEnemies() {
        enemies.clear();
        for (MapObject obj : map.getLayers().get("objects").getObjects()) {
            if (!obj.getProperties().containsKey(TiledMapManager.ENEMY)) continue;

            float x = getX(obj);
            float y = getY(obj);

            int type = 1;
            if (obj.getProperties().get("enemy_type") != null) {
                try { type = Integer.parseInt(obj.getProperties().get("enemy_type").toString().trim()); }
                catch (Exception ignored) {}
            }
            Enemy e = new Enemy(type);
            e.speed = (type == 2) ? 1.8f : 1.0f;
            e.position.set(x, y);
            enemies.add(e);
        }
    }

    private void loadPlatforms() {
        platforms.clear();
        for (MapObject obj : map.getLayers().get("objects").getObjects()) {
            if (!obj.getProperties().containsKey(TiledMapManager.MOBILE)) continue;

            float x = getX(obj);
            float y = getY(obj);

            int offset = Integer.parseInt(obj.getProperties().get("offset").toString());
            boolean right = Boolean.parseBoolean(obj.getProperties().get("right_direction").toString());

            Platform p = new Platform(x, y,
                TiledMapManager.PLATFORM_WIDTH, TiledMapManager.PLATFORM_HEIGHT,
                offset, right ? Direction.RIGHT : Direction.LEFT);
            platforms.add(p);
        }
    }

    private void loadFruits() {
        fruits.clear();
        for (MapObject obj : map.getLayers().get("objects").getObjects()) {
            if (!obj.getProperties().containsKey(TiledMapManager.FRUIT)) continue;
            Rectangle r = getRect(obj);
            fruits.add(r);
        }
    }

    // Helpers
    private static float getX(MapObject o){
        return (o instanceof RectangleMapObject)
            ? ((RectangleMapObject)o).getRectangle().x
            : o.getProperties().get("x", Float.class);
    }
    private static float getY(MapObject o){
        return (o instanceof RectangleMapObject)
            ? ((RectangleMapObject)o).getRectangle().y
            : o.getProperties().get("y", Float.class);
    }
    private static Rectangle getRect(MapObject o) {
        if (o instanceof RectangleMapObject) return ((RectangleMapObject)o).getRectangle();
        Rectangle r = new Rectangle();
        r.set(getX(o), getY(o), 16, 16);
        return r;
    }

    public void clearCharactersCurrentLevel() {
        enemies.clear();
        platforms.clear();
        fruits.clear();
    }

    public void removeCoin(int x, int y) {
        TiledMapTileLayer layer = TiledMapManager.collisionLayer;
        layer.setCell(x, y, null);
        currentCoins++;
    }
    public boolean hasLevel(int level) {
        String path = LEVEL_DIR + "/" + LEVEL_PREFIX + level + LEVEL_EXTENSION; // p.ej. levels/level3.tmx
        return Gdx.files.internal(path).exists();
    }

    public boolean advanceToNextLevelIfExists() {
        int next = currentLevel + 1;
        if (hasLevel(next)) {
            currentLevel = next;
            return true; // hay siguiente nivel
        }
        return false;     // no hay más niveles
    }
}
