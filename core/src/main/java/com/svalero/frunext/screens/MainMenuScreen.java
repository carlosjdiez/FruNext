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

public class MainMenuScreen implements Screen {
    final FruNext game;
    private Stage stage;

    public MainMenuScreen(FruNext game) {
        this.game = game;
    }

    @Override public void show() {
        stage = new Stage();
        Table table = new Table(game.getSkin());
        table.setFillParent(true);
        table.center();

        Label title = new Label("FRUNEXT\nMAIN MENU", game.getSkin());
        title.setFontScale(2.5f);

        TextButton play = new TextButton("PLAY GAME", game.getSkin());
        play.addListener(new ClickListener(){
            @Override public void touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int p, int b){
                game.setScreen(new GameScreen(game));
            }
        });

        TextButton instructions = new TextButton("INSTRUCTIONS", game.getSkin());
        instructions.addListener(new ClickListener(){
            @Override public void touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int p, int b){
                game.setScreen(new InstructionsScreen(game));
            }
        });

        TextButton options = new TextButton("OPTIONS", game.getSkin());
        options.addListener(new ClickListener(){
            @Override public void touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int p, int b){
                game.setScreen(new OptionsScreen(game));
            }
        });

        TextButton exit = new TextButton("QUIT GAME", game.getSkin());
        exit.addListener(new ClickListener(){
            @Override public void touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int p, int b){
                Gdx.app.exit();
            }
        });

        table.row().height(200); table.add(title).center().pad(35f);
        table.row().height(75);  table.add(play).center().width(500).pad(5f);
        table.row().height(75);  table.add(instructions).center().width(500).pad(5f);
        table.row().height(75);  table.add(options).center().width(500).pad(5f);
        table.row().height(75);  table.add(exit).center().width(500).pad(5f);

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
