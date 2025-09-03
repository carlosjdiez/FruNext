package com.svalero.frunext.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.svalero.frunext.FruNext;
import com.svalero.frunext.managers.ResourceManager;

public class OptionsScreen implements Screen {
    final FruNext game;
    private Stage stage;

    public OptionsScreen(FruNext game) { this.game = game; }

    @Override public void show() {
        stage = new Stage();

        Table table = new Table(game.getSkin());
        table.setFillParent(true);
        table.center();

        Label title = new Label("OPTIONS", game.getSkin());
        title.setFontScale(2.2f);

        final TextButton musicBtn = new TextButton(
            "MUSIC: " + (ResourceManager.musicEnabled ? "ON" : "OFF"),
            game.getSkin());
        musicBtn.addListener(new ClickListener(){
            @Override public void touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int p, int b){
                ResourceManager.musicEnabled = !ResourceManager.musicEnabled;
                musicBtn.setText("MUSIC: " + (ResourceManager.musicEnabled ? "ON" : "OFF"));
            }
        });

        final TextButton sfxBtn = new TextButton(
            "SFX: " + (ResourceManager.sfxEnabled ? "ON" : "OFF"),
            game.getSkin());
        sfxBtn.addListener(new ClickListener(){
            @Override public void touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int p, int b){
                ResourceManager.sfxEnabled = !ResourceManager.sfxEnabled;
                sfxBtn.setText("SFX: " + (ResourceManager.sfxEnabled ? "ON" : "OFF"));
            }
        });

        TextButton back = new TextButton("BACK", game.getSkin());
        back.addListener(new ClickListener(){
            @Override public void touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int p, int b){
                game.setScreen(new MainMenuScreen(game));
            }
        });

        table.row().height(150); table.add(title).center().pad(35f);
        table.row().height(75);  table.add(musicBtn).center().width(500).pad(5f);
        table.row().height(75);  table.add(sfxBtn).center().width(500).pad(5f);
        table.row().height(75);  table.add(back).center().width(500).pad(5f);

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { }
    @Override public void hide() { }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void dispose() { }
}
