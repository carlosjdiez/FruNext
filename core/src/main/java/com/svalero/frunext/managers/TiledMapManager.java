package com.svalero.frunext.managers;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

public class TiledMapManager {

    public static final String COIN = "coin";
    public static final String BLOCKED = "blocked";
    public static final String ENEMY = "enemy";
    public static final String FRUIT = "fruit";
    public static final String ANIMATION = "animation";
    public static final String MOBILE = "mobile";


    public static com.svalero.frunext.characters.Player playerRef;

    public static TiledMapTileLayer collisionLayer;
    public static MapLayer objectLayer;
    public static LevelManager levelManager;

    // Tamaños plataforma
    public static final int PLATFORM_WIDTH = 50;
    public static final int PLATFORM_HEIGHT = 12;

    public static void setLevelManager(LevelManager lm) { levelManager = lm; }

    // Versión con frameDuration
    public static void animateTiles(String animationString, int n, float frameDuration) {
        Array<StaticTiledMapTile> frames = new Array<>(n);
        Iterator<TiledMapTile> it = TiledMapManager.levelManager.map.getTileSets().getTileSet("tileset").iterator();
        while (it.hasNext()) {
            TiledMapTile tile = it.next();
            if (tile.getProperties().containsKey(ANIMATION) &&
                animationString.equals(tile.getProperties().get(ANIMATION, String.class))) {
                frames.add((StaticTiledMapTile) tile);
            }
        }

        AnimatedTiledMapTile animated = new AnimatedTiledMapTile(frameDuration, frames);
        for (TiledMapTile t : frames) animated.getProperties().putAll(t.getProperties());

        for (int x = 0; x < collisionLayer.getWidth(); x++) {
            for (int y = 0; y < collisionLayer.getHeight(); y++) {
                Cell cell = collisionLayer.getCell(x, y);
                if (cell == null) continue;
                if (cell.getTile().getProperties().containsKey(ANIMATION) &&
                    animationString.equals(cell.getTile().getProperties().get(ANIMATION, String.class))) {
                    cell.setTile(animated);
                }
            }
        }
    }

    public static void animateTiles(String animationString, int n) {
        animateTiles(animationString, n, 0.25f);
    }

    public static TiledMapTile getEmptyBox(TiledMap map) { return null; }
}
