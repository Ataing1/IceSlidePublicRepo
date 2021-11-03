package com.andrew.game.screens;

import com.andrew.game.screens.menu.BaseMenuScreen;
import com.andrew.game.screens.menu.MainMenuScreen;
import com.andrew.game.slip.Assets;
import com.andrew.game.slip.Slip;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class AboutScreen implements Screen {

    private final Slip game;
    private final Stage stage;
    private final Assets assets;

    public AboutScreen(Slip game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        assets = game.getAssets();

    }

    @Override
    public void show() {

        assets.loadSkin();
        FileHandle credits = Gdx.files.internal(Assets.CREDITS);
        assets.manager.finishLoading();
        final Skin mySkin = assets.getSkin();

        Table table = new Table();
        //table.setFillParent(true);
        table.defaults().width(Gdx.graphics.getWidth() / 1.3f);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = assets.titleFont;
        Label title = new Label("Developed by Andrew Taing", labelStyle);
        title.setWrap(true);
        title.setAlignment(Align.center);


        Label text = new Label("", new Label.LabelStyle(assets.textFont, Color.WHITE));
        text.setWidth(Gdx.graphics.getWidth() / 1.3f);
        text.setWrap(true);
        //todo update this with the new google review api
        text.setText("Hi guys, thanks for helping me beta test my game! I hope you guys are enjoying it. " +
                "If you have any ideas on how to improve the game, click the button and it will take you to a google form! Thanks again.");


        String creditsArtists = credits.readString();

        TextButton close = new TextButton("Back", mySkin);
        close.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
                return true;
            }
        });

        TextButton test = new TextButton("Feedback", mySkin);
        test.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                final Dialog dialog = new Dialog("", mySkin) {
                    @Override
                    protected void result(Object object) {
                        Gdx.net.openURI("https://forms.gle/DpTyYFRbbf8hD8qr7");
                    }
                };
                dialog.pad(BaseMenuScreen.DEFAULT_UNIT);
                dialog.addListener(new InputListener() {
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        if (x < 0 || x > dialog.getWidth() || y < 0 || y > dialog.getHeight()) {
                            dialog.hide();
                            return true;
                        }
                        return false;
                    }
                });
                dialog.text("taking you to feedback form");

                dialog.button("Go!", "GO");
                dialog.show(stage);
                return true;
            }
        });

        Label creditsText = new Label(creditsArtists, new Label.LabelStyle(assets.textFont, Color.WHITE));
        creditsText.setWrap(true);
        table.defaults().pad(BaseMenuScreen.DEFAULT_UNIT);
        table.add(title).row();
        table.add(text).row();
        table.add(test).row();
        table.add(creditsText).row();
        //table.add(close).height(150).row();


        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setScrollingDisabled(true, false);
        //scrollPane.setFillParent(true);

        Table stageTable = new Table();
        stageTable.setFillParent(true);
        stageTable.add(close).pad(BaseMenuScreen.DEFAULT_UNIT * 3).grow();
        stageTable.add(scrollPane).padRight(BaseMenuScreen.DEFAULT_UNIT * 3).padTop(BaseMenuScreen.DEFAULT_UNIT * 3).padBottom(BaseMenuScreen.DEFAULT_UNIT * 3);

        Gdx.input.setInputProcessor(stage);
        stage.addActor(stageTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.290196078f,
                0.31372549f,
                0.262745098f
                , 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        assets.unloadSkin();
    }
}
