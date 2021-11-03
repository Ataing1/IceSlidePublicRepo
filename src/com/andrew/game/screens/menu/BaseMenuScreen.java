package com.andrew.game.screens.menu;

import com.andrew.game.UI.MenuBar;
import com.andrew.game.UI.ParallaxBackground;
import com.andrew.game.UI.SignInUI;
import com.andrew.game.UI.TapCloseDialog;
import com.andrew.game.enums.MenuBarEvent;
import com.andrew.game.enums.NavigationEvent;
import com.andrew.game.interfaces.MyCallbackBoolean;
import com.andrew.game.interfaces.MyCallbackMenuBar;
import com.andrew.game.screens.AboutScreen;
import com.andrew.game.screens.AccountScreen;
import com.andrew.game.slip.Assets;
import com.andrew.game.slip.Menu;
import com.andrew.game.slip.Slip;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Date;

public class BaseMenuScreen implements Screen {
    public static final float DEFAULT_UNIT = 8.278095238f * Gdx.graphics.getDensity();
    public static final float BUTTON_SIZE = 46.86857181f * Gdx.graphics.getDensity();
    protected final Slip game;
    protected final Stage stage;
    protected final Assets assets;
    protected final Menu menu;
    protected Music mainMenuSound;
    protected Sound clickSound;
    Array<Texture> textureArray = new Array<>(5);
    protected Window settingsWindow;
    protected Skin mySkin;
    protected Table topParent;
    protected static final int TOP_PARENT_COLUMN = 3;

    public BaseMenuScreen(Slip game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        assets = game.getAssets();
        menu = new Menu(game.getPreferences());
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        assets.loadSkin();
        assets.manager.load(Assets.MAIN_MENU_SOUND, Music.class);
        assets.manager.load(Assets.CLICK_SOUND, Sound.class);
        assets.manager.finishLoading();
        mySkin = assets.getSkin();
        mainMenuSound = assets.manager.get(Assets.MAIN_MENU_SOUND, Music.class);
        clickSound = assets.manager.get(Assets.CLICK_SOUND, Sound.class);
        mainMenuSound.setLooping(true);

        settingsWindow = menu.getWindow(mySkin);

        for (int i = 0; i < 5; i++) {
            textureArray.add(new Texture(Gdx.files.internal("images/parrallax_" + i + ".png")));
        }
        ParallaxBackground parallaxBackground = new ParallaxBackground(textureArray);
        parallaxBackground.setSpeed(1);

        Table topTable = new Table();
        topTable.defaults().space(DEFAULT_UNIT);
        topTable.add(game.accountManager.getCurrencyObject().getCurrencyTable(mySkin, assets)).padLeft(DEFAULT_UNIT);
        topTable.add(getNavigationTable(mySkin)).growX();
        topTable.add(getMenuButton(mySkin)).size(BUTTON_SIZE).padRight(DEFAULT_UNIT);
        topTable.validate();
        topTable.setBackground(mySkin.getDrawable("NoTitlePurpleBlue64"));

        topParent = new Table();
        topParent.add(topTable).growX().colspan(TOP_PARENT_COLUMN);
        topParent.setFillParent(true);
        topParent.top();
        topParent.row();
        //topParent.setDebug(true, true);
        stage.addActor(topParent);
        stage.addActor(parallaxBackground);
        stage.addActor(settingsWindow);
        //stage.setDebugAll(true);
        parallaxBackground.toBack();//set to back, so it doesn't cover the other actors
    }

    @Override
    public void resize(int width, int height) {
        //topParent.validate();
        topParent.layout();
        settingsWindow.toFront(); //set to front becuase it shouild cover all other actors with its modal window

    }

    protected Table getNavigationTable(Skin mySkin) {
        Table table = new Table();
        table.defaults().space(DEFAULT_UNIT).growX();//.uniform().grow();
        table.add(getMainMenuButton(mySkin),
                getCharacterSelectbutton(mySkin),
                getSpriteShopButton(mySkin),
                getShopActor(mySkin));
        return table;
    }

    protected TextButton getMainMenuButton(Skin mySkin) {
        TextButton textButton = new TextButton("Lobby", mySkin, "gold");
        addListener(textButton, NavigationEvent.MAIN_MENU);
        return textButton;
    }

    protected TextButton getCharacterSelectbutton(Skin mySkin) {
        TextButton textButton = new TextButton("Collection", mySkin, "gold");
        addListener(textButton, NavigationEvent.CHARACTER_SELECT);
        return textButton;
    }

    protected TextButton getSpriteShopButton(Skin mySkin) {
        TextButton textButton = new TextButton("Sprite Shop", mySkin, "gold");
        addListener(textButton, NavigationEvent.SPRITE_SHOP);

        Date date = game.accountManager.getAccountObject().getDailyRewardDate();
        int dailyDecision = game.fireBaseServices.checkDaily(date);
        if (dailyDecision == 0 || dailyDecision == 1) {
            textButton.addAction(
                    Actions.forever(
                            Actions.sequence(
                                    Actions.fadeIn(0f),
                                    Actions.fadeOut(1f)
                            )));
        }
        return textButton;
    }

    protected TextButton getShopActor(Skin mySkin) {
        TextButton textButton = new TextButton("Store", mySkin, "gold");
        addListener(textButton, NavigationEvent.STORE);
        return textButton;
    }

    protected void addListener(TextButton textButton, final NavigationEvent navigationEvent) {
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buttonClickedSound();
                switchScreen(navigationEvent);
            }
        });
    }

    protected void buttonClickedSound() {
        if (menu.playFX) clickSound.play(.3f);
    }


    /**
     * override in subclass so that if nav event is current screen, return;
     *
     * @param navigationEvent the id linked to each button
     */
    protected void switchScreen(NavigationEvent navigationEvent) {
        switch (navigationEvent) {
            case STORE:
                game.setScreen(new StoreScreen(game));
                dispose();
                break;
            case MAIN_MENU:
                game.setScreen(new MainMenuScreen(game));
                dispose();
                break;
            case SPRITE_SHOP:
                game.setScreen(new SpriteShopScreen(game));
                dispose();
                break;
            case CHARACTER_SELECT:
                game.setScreen(new CharacterSelectionScreen(game));
                dispose();
                break;
        }
    }


    private ImageButton getMenuButton(final Skin mySkin) {
        ImageButton imageButton = new ImageButton(mySkin, "sandwhich");
        imageButton.setSize(BUTTON_SIZE, BUTTON_SIZE);
        imageButton.setPosition(Gdx.graphics.getWidth() - imageButton.getPrefWidth() / 2f, Gdx.graphics.getHeight() - imageButton.getPrefHeight() / 2f);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buttonClickedSound();
                new MenuBar(mySkin, new MyCallbackMenuBar() {
                    @Override
                    public void onCallback(MenuBarEvent menuBarEvent) {
                        switch (menuBarEvent) {
                           /* case EXIT:
                                Gdx.app.exit();
                                break;*/
                            case CREDIT:
                                game.setScreen(new AboutScreen(game));
                                dispose();
                                break;
                            case ACCOUNT:
                                if (game.fireBaseServices.isAnonymous()) {
                                    new SignInUI(mySkin, game).show(stage);
                                } else {
                                    game.setScreen(new AccountScreen(game));
                                    dispose();
                                }
                                break;
                            case SETTING:
                                settingsWindow.setVisible(true);
                                break;
                            case FEEDBACK:
                                feedback(mySkin);
                                break;
                        }
                    }
                }).show(stage);
            }
        });
        return imageButton;
    }

    private void feedback(Skin mySkin) {

        TapCloseDialog dialog = new TapCloseDialog("", mySkin) {
            @Override
            protected void result(Object object) {
                if (object.equals("GO")) {
                    game.serviceInterface.getInAppReview(new MyCallbackBoolean() {
                        @Override
                        public void onCallback(boolean status) {
                            if(!status){
                                Gdx.net.openURI("https://forms.gle/DpTyYFRbbf8hD8qr7");
                            }
                        }
                    });

                    //Gdx.net.openURI("https://forms.gle/DpTyYFRbbf8hD8qr7");
                }
            }
        };
        dialog.pad(BaseMenuScreen.DEFAULT_UNIT);
        dialog.text("Taking you to feedback form");
        dialog.getButtonTable().defaults().space(DEFAULT_UNIT).uniform().growX();
        dialog.button("Go!", "GO");
        dialog.button("Close", "CLOSE");
        dialog.show(stage);
    }


    public void render(float delta) {

       /* Gdx.gl.glClearColor(0.290196078f,
                0.31372549f,
                0.262745098f
                , 1);*/
        Gdx.gl.glClearColor(.1137f,
                .0745f,
                0.2078f
                , 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (menu.playMusic) {
            mainMenuSound.play();
        } else {
            mainMenuSound.pause();
        }

        stage.act();
        game.accountManager.update(delta);
        stage.draw();

    }

    @Override
    public void dispose() {
        stage.dispose();
        assets.unloadSkin();

        //restart game, go to story level, and play the latest level, you will hear 2 sounds
        assets.manager.unload(Assets.MAIN_MENU_SOUND);
        assets.manager.unload(Assets.CLICK_SOUND);
        for (Texture x : textureArray) {
            x.dispose();
        }
       /* backgroundTexture.dispose();
        backgroundTexture1.dispose();
        backgroundTexture2.dispose();*/
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


}
