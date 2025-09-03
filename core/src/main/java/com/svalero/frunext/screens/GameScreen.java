package com.svalero.frunext.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.svalero.frunext.FruNext;
import com.svalero.frunext.managers.SpriteManager;

public class GameScreen implements Screen {
    final FruNext game;
    public SpriteManager spriteManager;

    public GameScreen(FruNext game) {
        this.game = game;
        spriteManager = new SpriteManager(game);
        game.paused = false;
    }

    @Override public void show() { game.paused = false; }

    @Override public void render(float dt) {
        if (!game.paused) spriteManager.update(dt);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteManager.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new InGameMenuScreen(game, this));
        }
    }

    @Override public void hide() { game.paused = true; }
    @Override public void dispose() { spriteManager.dispose(); }
    @Override public void resize(int width, int height) { spriteManager.resize(width, height); }
    @Override public void pause() { }
    @Override public void resume() { game.paused = false; }
}
