package com.svalero.frunext.characters;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.svalero.frunext.managers.ResourceManager;

public class Platform {
    public Vector2 originalPosition = new Vector2();
    public Vector2 position = new Vector2();
    public Vector2 velocity = new Vector2();
    public float width, height;
    private int offset;
    private Direction direction;
    private Direction currentDirection;

    public enum Direction { RIGHT, LEFT; }
    private float SPEED = 30f;

    public Platform(float x, float y, float width, float height, int offset, Direction direction) {
        this.originalPosition.set(x, y);
        this.position.set(x, y);
        this.width = width;
        this.height = height;
        this.offset = offset;
        this.direction = direction;
        this.currentDirection = direction;
    }

    public void render(Batch batch) {
        batch.draw(ResourceManager.getAtlas("items/items.pack").findRegion("platform"),
            position.x, position.y, width, height);
    }

    public void update(float dt) {
        velocity.x = (currentDirection == Direction.RIGHT) ? SPEED : -SPEED;
        position.x += velocity.x * dt;

        if (direction == Direction.RIGHT) {
            if (currentDirection == Direction.RIGHT) {
                if (position.x >= (originalPosition.x + offset)) currentDirection = Direction.LEFT;
            } else {
                if (position.x <= originalPosition.x) currentDirection = Direction.RIGHT;
            }
        } else {
            if (currentDirection == Direction.LEFT) {
                if (position.x < (originalPosition.x - offset)) currentDirection = Direction.RIGHT;
            } else {
                if (position.x > originalPosition.x) currentDirection = Direction.LEFT;
            }
        }
    }
}
