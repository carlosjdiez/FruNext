package com.svalero.frunext.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.svalero.frunext.FruNext;

public class InstructionsScreen implements Screen {
    final FruNext game;
    private Stage stage;

    public InstructionsScreen(FruNext game) { this.game = game; }

    @Override public void show() {
        stage = new Stage();

        Table table = new Table(game.getSkin());
        table.setFillParent(true);
        table.pad(20);

        Label title = new Label("INSTRUCTIONS", game.getSkin());
        title.setFontScale(2.2f);

        Label text = new Label(
                "Objetivo: recoge la fruta para completar el nivel.\n\n" +
                        "Controles:\n" +
                        " - Flechas IZQ / DER: mover\n" +
                        " - ESPACIO: saltar\n" +
                        " - ESC: abrir/cerrar menú en partida\n\n" +
                        "Menú en partida:\n" +
                        " - Resume, Sound ON/OFF, Main Menu, Quit",
                game.getSkin());
        text.setWrap(true);
        text.setAlignment(Align.topLeft);

        ScrollPane scroll = new ScrollPane(text, game.getSkin());

        TextButton back = new TextButton("BACK", game.getSkin());
        back.addListener(new ClickListener(){
            @Override public void touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int p, int b){
                game.setScreen(new MainMenuScreen(game));
            }
        });

        table.add(title).padBottom(20).center().row();
        table.add(scroll).width(700).height(330).padBottom(15).row();
        table.add(back).width(300).height(60).center();

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
