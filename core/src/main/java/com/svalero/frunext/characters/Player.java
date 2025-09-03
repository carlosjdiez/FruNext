package com.svalero.frunext.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.svalero.frunext.managers.ResourceManager;
import com.svalero.frunext.managers.TiledMapManager;
import com.svalero.frunext.managers.SpriteManager;

public class Player {
    public Vector2 position = new Vector2();
    public Vector2 velocity = new Vector2();
    public boolean canJump;
    public boolean isRunning;
    public boolean isJumping;

    public static Platform stuckPlatform;

    private TextureRegion idleLeft, idleRight;
    private TextureRegion walkLeftStand, walkRightStand;
    private TextureRegion currentFrame;
    private Animation<TextureRegion> rightAnim, leftAnim;
    private float stateTime = 0f;
    private final SpriteManager spriteManager;

    public enum State { IDLE_LEFT, IDLE_RIGHT, RUNNING_LEFT, RUNNING_RIGHT }
    public State state = State.IDLE_RIGHT;

    private final Array<Rectangle> tiles = new Array<>();
    private final Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override protected Rectangle newObject() { return new Rectangle(); }
    };

    public static float WALKING_SPEED = 120f;
    public static float JUMPING_SPEED = 260f;
    public static float GRAVITY = 900f;

    public static float SCALE = 1.0f;
    public static float WIDTH;
    public static float HEIGHT;
    private static final float FOOT_RENDER_OFFSET = -2f;

    public Player(SpriteManager spriteManager) {
        this.spriteManager = spriteManager;
        TextureAtlas atlas = ResourceManager.getAtlas("characters/characters.pack");
        idleLeft = atlas.findRegion("knight_idle_left");
        idleRight = atlas.findRegion("knight_idle_right");
        walkLeftStand = atlas.findRegion("knight_walk_left1");
        walkRightStand = atlas.findRegion("knight_walk_right1");

        Array<TextureRegion> left = new Array<>();
        left.add(atlas.findRegion("knight_walk_left1"));
        left.add(atlas.findRegion("knight_walk_left2"));
        left.add(atlas.findRegion("knight_walk_left3"));
        leftAnim = new Animation<>(0.15f, left, Animation.PlayMode.LOOP);

        Array<TextureRegion> right = new Array<>();
        right.add(atlas.findRegion("knight_walk_right1"));
        right.add(atlas.findRegion("knight_walk_right2"));
        right.add(atlas.findRegion("knight_walk_right3"));
        rightAnim = new Animation<>(0.15f, right, Animation.PlayMode.LOOP);

        int w = idleRight.getRegionWidth();
        int h = idleRight.getRegionHeight();
        WIDTH = w * SCALE;
        HEIGHT = h * SCALE;
    }

    public void render(Batch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        switch (state) {
            case IDLE_LEFT: currentFrame = idleLeft; break;
            case IDLE_RIGHT: currentFrame = idleRight; break;
            case RUNNING_LEFT: currentFrame = isJumping ? walkLeftStand : leftAnim.getKeyFrame(stateTime, true); break;
            case RUNNING_RIGHT: currentFrame = isJumping ? walkRightStand : rightAnim.getKeyFrame(stateTime, true); break;
        }
        batch.draw(currentFrame, MathUtils.floor(position.x), MathUtils.floor(position.y + FOOT_RENDER_OFFSET), WIDTH, HEIGHT);
    }

    public void update(float dt) {
        if (Player.stuckPlatform != null) {
            position.x += Player.stuckPlatform.velocity.x * dt;
        } else {
            velocity.y -= GRAVITY * dt;
        }

        float dx = velocity.x * dt;
        float dy = velocity.y * dt;

        Rectangle playerRect = rectPool.obtain();
        playerRect.set(position.x, position.y, WIDTH, HEIGHT);
        int startX, endX, startY, endY;

        // X
        if (dx > 0) startX = endX = (int)(position.x + WIDTH + dx);
        else startX = endX = (int)(position.x + dx);
        startY = (int)position.y;
        endY = (int)position.y + (int)HEIGHT;
        getSolidTiles(startX, startY, endX, endY, tiles);
        playerRect.x += dx;
        for (Rectangle tile : tiles) {
            if (playerRect.overlaps(tile)) {
                if (dx > 0) position.x = tile.x - WIDTH;
                else position.x = tile.x + tile.width;
                dx = 0;
                break;
            }
        }
        playerRect.x = position.x;

        // Y
        if (dy > 0) startY = endY = (int)(position.y + HEIGHT + dy);
        else startY = endY = (int)(position.y + dy);
        startX = (int)position.x;
        endX = (int)(position.x + WIDTH);
        getSolidTiles(startX, startY, endX, endY, tiles);
        playerRect.y += dy;
        for (Rectangle tile : tiles) {
            if (playerRect.overlaps(tile)) {
                if (dy > 0) {
                    position.y = tile.y - HEIGHT;
                } else {
                    position.y = tile.y + tile.height;
                    canJump = true;
                    isJumping = false;
                }
                dy = 0;
                velocity.y = 0;
                break;
            }
        }
        rectPool.free(playerRect);

        position.x += dx;
        position.y += dy;

        if (position.y < 0) spriteManager.playerDie();
        if (position.x < 0) position.x = 0;
    }

    /** Salta si puede */
    public void tryJump() {
        Player.stuckPlatform = null;
        if (canJump) {
            if (ResourceManager.sfxEnabled) ResourceManager.getSound("sounds/jump.wav").play();
            velocity.y = JUMPING_SPEED;
            canJump = false;
            isJumping = true;
        }
    }

    /** Colisiones con tiles sólidos y recogida de moneda */
    private void getSolidTiles(int startX, int startY, int endX, int endY, Array<Rectangle> out) {
        out.clear();
        float tw = TiledMapManager.collisionLayer.getTileWidth();
        float th = TiledMapManager.collisionLayer.getTileHeight();
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                int xCell = (int)(x / tw);
                int yCell = (int)(y / th);
                Cell cell = TiledMapManager.collisionLayer.getCell(xCell, yCell);
                if (cell == null) continue;

                if (cell.getTile().getProperties().containsKey(TiledMapManager.BLOCKED)) {
                    Rectangle r = rectPool.obtain();
                    r.set(xCell * tw, yCell * th, tw, th);
                    out.add(r);
                } else if (cell.getTile().getProperties().containsKey(TiledMapManager.COIN)) {
                    if (ResourceManager.sfxEnabled) ResourceManager.getSound("sounds/coin.wav").play();
                    TiledMapManager.levelManager.removeCoin(xCell, yCell);
                }
            }
        }
    }
}
