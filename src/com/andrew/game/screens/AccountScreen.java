package com.andrew.game.screens;

import com.andrew.game.interfaces.MyCallbackBoolean;
import com.andrew.game.screens.gameplay.TutorialScreen;
import com.andrew.game.screens.menu.BaseMenuScreen;
import com.andrew.game.screens.menu.MainMenuScreen;
import com.andrew.game.slip.Assets;
import com.andrew.game.slip.Slip;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class AccountScreen implements Screen {

    private final Slip game;
    private final Stage stage;
    private final Assets assets;
    private Label welcomeLabel;

    public AccountScreen(Slip game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        assets = game.getAssets();
        System.out.println("entered account Screen");
    }

    @Override
    public void show() {
        assets.loadSkin();
        assets.manager.finishLoading();
        Skin mySkin = assets.getSkin();
        Gdx.input.setInputProcessor(stage);
        welcomePage(mySkin);
    }

    private void getGreeting() {

        welcomeLabel.setText("Welcome " + game.accountManager.getAccountObject().getUsername());
    }

    private void welcomePage(final Skin mySkin) {

        welcomeLabel = new Label("", new Label.LabelStyle(assets.titleFont, Color.WHITE));
        TextButton home = new TextButton("Back", mySkin);
        home.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });
        TextButton logOut = new TextButton("Log Out", mySkin);
        logOut.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new Dialog("Logging Out", mySkin).show(stage);
                game.fireBaseServices.saveData();
                game.fireBaseServices.logOut(new MyCallbackBoolean() {
                    @Override
                    public void onCallback(boolean status) {
                        if (status) {
                            game.setScreen(new TutorialScreen(game, 1));
                            dispose();
                        }
                    }
                });
            }
        });

        getGreeting();
        Table table = new Table();
        table.setFillParent(true);
        table.top();
        table.defaults().pad(BaseMenuScreen.DEFAULT_UNIT * 3);
        table.add(welcomeLabel).colspan(2).row();
        table.add(home).grow();
        table.add(logOut).grow();
        stage.addActor(table);
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
        stage.clear();
        stage.dispose();
        assets.unloadSkin();
    }
}
