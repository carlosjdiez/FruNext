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

public class InGameMenuScreen implements Screen {
    FruNext game;
    GameScreen gameScreen;
    Stage stage;

    public InGameMenuScreen(FruNext game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
    }

    @Override public void show() {
        stage = new Stage();
        Table table = new Table(game.getSkin());
        table.setFillParent(true);
        table.center();

        Label title = new Label("PAUSE", game.getSkin());
        title.setFontScale(2.5f);

        TextButton resume = new TextButton("RESUME", game.getSkin());
        resume.addListener(new ClickListener(){
            @Override public void touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int p, int b){
                dispose();
                game.setScreen(gameScreen);
            }
        });

        String soundLabel = ResourceManager.musicEnabled ? "SOUND: ON" : "SOUND: OFF";
        TextButton toggleSound = new TextButton(soundLabel, game.getSkin());
        toggleSound.addListener(new ClickListener(){
            @Override public void touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int p, int b){
                ResourceManager.musicEnabled = !ResourceManager.musicEnabled;
                if (gameScreen != null && gameScreen.spriteManager != null && gameScreen.spriteManager.music != null) {
                    if (ResourceManager.musicEnabled) gameScreen.spriteManager.music.play();
                    else gameScreen.spriteManager.music.pause();
                }
                ((TextButton)e.getListenerActor()).setText(ResourceManager.musicEnabled ? "SOUND: ON" : "SOUND: OFF");
            }
        });

        TextButton mainMenu = new TextButton("RETURN TO MAIN MENU", game.getSkin());
        mainMenu.addListener(new ClickListener(){
            @Override public void touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int p, int b){
                dispose();
                gameScreen.dispose();
                game.setScreen(new MainMenuScreen(game));
            }
        });

        TextButton exit = new TextButton("QUIT GAME", game.getSkin());
        exit.addListener(new ClickListener(){
            @Override public void touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int p, int b){
                dispose();
                Gdx.app.exit();
            }
        });

        table.row().height(150); table.add(title).center().pad(35f);
        table.row().height(70);  table.add(resume).center().width(300).pad(5f);
        table.row().height(70);  table.add(toggleSound).center().width(300).pad(5f);
        table.row().height(70);  table.add(mainMenu).center().width(300).pad(5f);
        table.row().height(70);  table.add(exit).center().width(300).pad(5f);

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    @Override public void render(float dt) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(dt);
        stage.draw();
    }

    @Override public void dispose() { }
    @Override public void hide() { }
    @Override public void pause() { }
    @Override public void resize(int width, int height) { }
    @Override public void resume() { }
}
