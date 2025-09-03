package com.svalero.frunext;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.svalero.frunext.screens.SplashScreen;

public class FruNext extends Game {
    private Skin skin;
    public Batch batch;
    public BitmapFont font;
    public boolean paused;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font  = new BitmapFont(Gdx.files.internal("ui/default.fnt"));
        setScreen(new SplashScreen(this));
    }

    public Skin getSkin() {
        if (skin == null) skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        return skin;
    }

    @Override
    public void render() { super.render(); }
    @Override
    public void resize(int w, int h) { getScreen().resize(w, h); }
    @Override
    public void pause()  { paused = true; }
    @Override
    public void resume() { paused = false; }
    @Override
    public void dispose() { getScreen().dispose(); }
}
