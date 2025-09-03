package com.svalero.frunext.characters;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.svalero.frunext.managers.ResourceManager;
import com.svalero.frunext.managers.TiledMapManager;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

public class Item implements Disposable {
    public Vector2 position = new Vector2();
    public Vector2 velocity = new Vector2();
    public boolean isAlive = true;

    private TextureRegion currentFrame = new Sprite(ResourceManager.getAtlas("items/items.pack").findRegion("life"));
    private boolean faceLeft = true;

    public static float WALKING_SPEED = 1.0f;
    public static float JUMPING_SPEED = 5.0f;
    public static float GRAVITY = 9f;
    public static float WIDTH = 13;
    public static float HEIGHT = 13;

    private Array<Rectangle> tiles = new Array<>();
    private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override protected Rectangle newObject () { return new Rectangle(); }
    };

    public void render(Batch spriteBatch) {
        spriteBatch.draw(currentFrame, position.x, position.y, WIDTH, HEIGHT);
    }

    public void update(float dt) {
        velocity.x = faceLeft ? -WALKING_SPEED : WALKING_SPEED;
        if (position.x <= 0 || position.y <= 0) dispose();

        velocity.y -= GRAVITY * dt;
        if (velocity.y >  JUMPING_SPEED) velocity.y =  JUMPING_SPEED;
        else if (velocity.y < -JUMPING_SPEED) velocity.y = -JUMPING_SPEED;

        velocity.scl(dt);

        int startX, endX, startY, endY;
        Rectangle rect = rectPool.obtain();
        rect.set(position.x, position.y, 18, 18);

        // Y
        if (velocity.y > 0) startY = endY = (int) (position.y + HEIGHT + velocity.y);
        else startY = endY = (int) (position.y + velocity.y);
        startX = (int) position.x; endX = (int) (position.x + WIDTH);
        getTilesPosition(startX, startY, endX, endY, tiles);
        rect.y += velocity.y;
        for (Rectangle tile : tiles) {
            if (tile.overlaps(rect)) {
                if (velocity.y > 0) position.y = tile.y - HEIGHT;
                else position.y = tile.y + tile.height;
                velocity.y = 0; break;
            }
        }

        // X
        if (velocity.x > 0) startX = endX = (int) (position.x + WIDTH + velocity.x);
        else startX = endX = (int) (position.x + velocity.x);
        startY = (int) position.y; endY = (int) (position.y + HEIGHT);
        getTilesPosition(startX, startY, endX, endY, tiles);
        rect.x += velocity.x;
        for (Rectangle tile : tiles) {
            if (rect.overlaps(tile)) { faceLeft = !faceLeft; velocity.x = 0; break; }
        }

        rect.x = position.x;
        rectPool.free(rect);
        velocity.scl(1 / dt);
        position.add(velocity);
    }

    private void getTilesPosition(int startX, int startY, int endX, int endY, Array<Rectangle> tiles) {
        tiles.clear();
        float tw = TiledMapManager.collisionLayer.getTileWidth();
        float th = TiledMapManager.collisionLayer.getTileHeight();
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                int cx = (int) (x / tw);
                int cy = (int) (y / th);
                Cell cell = TiledMapManager.collisionLayer.getCell(cx, cy);
                if (cell != null && cell.getTile().getProperties().containsKey(TiledMapManager.BLOCKED)) {
                    Rectangle rect = rectPool.obtain();
                    rect.set((int) (Math.ceil(x / tw) * tw), (int) (Math.ceil(y / th) * th), 0, 0);
                    tiles.add(rect);
                }
            }
        }
    }

    @Override public void dispose() { isAlive = false; }
}
