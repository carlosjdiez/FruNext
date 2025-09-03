package com.svalero.frunext.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.svalero.frunext.FruNext;
import com.svalero.frunext.managers.ResourceManager;

public class SplashScreen implements Screen {
    private Texture splashTexture;
    private Image splashImage;
    private Stage stage;
    private boolean splashDone = false;
    private FruNext game;

    public SplashScreen(FruNext game) {
        this.game = game;
        splashTexture = new Texture(Gdx.files.internal("ui/splash.png"));
        splashImage = new Image(splashTexture);
        stage = new Stage();
    }

    @Override public void show() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        splashImage.addAction(Actions.sequence(
            Actions.alpha(0), Actions.fadeIn(1f), Actions.delay(1.0f),
            Actions.run(() -> splashDone = true)
        ));
        table.add(splashImage).center();
        stage.addActor(table);

        ResourceManager.loadAllResources();
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();

        if (ResourceManager.update()) {
            if (splashDone) {
                game.setScreen(new MainMenuScreen(game));
            }
        }
    }

    @Override public void resize(int w, int h) { }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { dispose(); }
    @Override public void dispose() { splashTexture.dispose(); stage.dispose(); }
}
