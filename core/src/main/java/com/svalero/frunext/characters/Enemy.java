package com.svalero.frunext.characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.math.Vector2;
import com.svalero.frunext.managers.ResourceManager;
import com.svalero.frunext.managers.TiledMapManager;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;


public class Enemy implements Disposable {
    public Vector2 position = new Vector2();
    public Vector2 velocity = new Vector2();
    public boolean isAlive = true;
    public float speed = 1.0f;
    private boolean faceLeft = true;

    // Para compatibilidad con código existente (pero NO los usamos en colisiones/render)
    public static int WIDTH = 24;
    public static int HEIGHT = 16;

    public static float GRAVITY = 9f;
    public static float JUMPING_SPEED = 3.5f;

    // tamaño por instancia y tipo guardado
    public int width;
    public int height;
    private final int type;
    public int getType() { return type; }

    // Animaciones
    private Animation<TextureRegion> anim;          // para type 1/2
    private Animation<TextureRegion> animLeft3;     // para clon (izquierda)
    private Animation<TextureRegion> animRight3;    // para clon (derecha)
    private float stateTime = 0f;

    private final Array<Rectangle> tiles = new Array<>();
    private final Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override protected Rectangle newObject () { return new Rectangle(); }
    };

    public Enemy(int type) {
        this.type = type;

        TextureAtlas atlas = ResourceManager.getAtlas("characters/characters.pack");

        if (type == 3) {
            // Clon del jugador: 32x32 y animación en ambos sentidos
            Array<TextureRegion> left = new Array<>();
            left.add(atlas.findRegion("knight_walk_left1"));
            left.add(atlas.findRegion("knight_walk_left2"));
            left.add(atlas.findRegion("knight_walk_left3"));
            animLeft3 = new Animation<>(0.15f, left, Animation.PlayMode.LOOP);

            Array<TextureRegion> right = new Array<>();
            right.add(atlas.findRegion("knight_walk_right1"));
            right.add(atlas.findRegion("knight_walk_right2"));
            right.add(atlas.findRegion("knight_walk_right3"));
            animRight3 = new Animation<>(0.15f, right, Animation.PlayMode.LOOP);

            this.width = 32;
            this.height = 32;
        } else {
            // Enemigos originales: 24x16
            Array<TextureRegion> frames = new Array<>();
            if (type == 2) {
                frames.add(atlas.findRegion("enemy2_walk1"));
                frames.add(atlas.findRegion("enemy2_walk2"));
                frames.add(atlas.findRegion("enemy2_walk3"));
                frames.add(atlas.findRegion("enemy2_walk4"));
            } else {
                frames.add(atlas.findRegion("enemy1_walk1"));
                frames.add(atlas.findRegion("enemy1_walk2"));
                frames.add(atlas.findRegion("enemy1_walk3"));
                frames.add(atlas.findRegion("enemy1_walk4"));
            }
            anim = new Animation<>(0.15f, frames, Animation.PlayMode.LOOP);
            this.width = 24;
            this.height = 16;
        }
    }

    public void render(Batch batch) {
        stateTime += com.badlogic.gdx.Gdx.graphics.getDeltaTime();
        TextureRegion frame;
        if (type == 3) {
            frame = faceLeft ? animLeft3.getKeyFrame(stateTime, true)
                : animRight3.getKeyFrame(stateTime, true);
        } else {
            frame = anim.getKeyFrame(stateTime, true);
        }
        batch.draw(frame, position.x, position.y, width, height);
    }

    public void update(float dt) {
        velocity.x = faceLeft ? -speed : speed;

        if (position.x <= 0) dispose();
        if (position.y <= 0) dispose();

        velocity.y -= GRAVITY * dt;
        if (velocity.y >  JUMPING_SPEED) velocity.y =  JUMPING_SPEED;
        else if (velocity.y < -JUMPING_SPEED) velocity.y = -JUMPING_SPEED;

        velocity.scl(dt);

        int startX, endX, startY, endY;
        Rectangle rect = rectPool.obtain();
        rect.set(position.x, position.y, width, height);

        // ---- Eje Y
        if (velocity.y > 0) startY = endY = (int)(position.y + height + velocity.y);
        else                startY = endY = (int)(position.y + velocity.y);
        startX = (int)position.x; endX = (int)(position.x + width);
        getTilesPosition(startX, startY, endX, endY, tiles);
        rect.y += velocity.y;
        for (Rectangle tile : tiles) {
            if (tile.overlaps(rect)) {
                if (velocity.y > 0) position.y = tile.y - height;
                else                 position.y = tile.y + tile.height;
                velocity.y = 0; break;
            }
        }

        // ---- Eje X
        if (velocity.x > 0) startX = endX = (int)(position.x + width + velocity.x);
        else                startX = endX = (int)(position.x + velocity.x);
        startY = (int)position.y; endY = (int)(position.y + height);
        getTilesPosition(startX, startY, endX, endY, tiles);
        rect.x += velocity.x;
        for (Rectangle tile : tiles) {
            if (rect.overlaps(tile)) {
                faceLeft = !faceLeft; // cambio de sentido al chocar
                velocity.x = 0; break;
            }
        }

        rect.x = position.x;
        rectPool.free(rect);
        velocity.scl(1 / dt);
        position.add(velocity);
    }

    private void getTilesPosition(int startX, int startY, int endX, int endY, Array<Rectangle> out) {
        out.clear();
        float tw = TiledMapManager.collisionLayer.getTileWidth();
        float th = TiledMapManager.collisionLayer.getTileHeight();
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                int xCell = (int)(x / tw);
                int yCell = (int)(y / th);
                Cell cell = TiledMapManager.collisionLayer.getCell(xCell, yCell);
                if ((cell != null) && (cell.getTile().getProperties().containsKey(TiledMapManager.BLOCKED))) {
                    Rectangle r = rectPool.obtain();
                    r.set((int)(Math.ceil(x / tw) * tw), (int)(Math.ceil(y / th) * th), 0, 0);
                    out.add(r);
                }
            }
        }
    }

    @Override public void dispose() { isAlive = false; }
}
