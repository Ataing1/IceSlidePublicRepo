package com.andrew.game.slip;

import com.andrew.game.UI.ClippedWindow;
import com.andrew.game.enums.MenuEvent;
import com.andrew.game.observer.SubjectMenu;
import com.andrew.game.screens.menu.BaseMenuScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;

import org.jetbrains.annotations.NotNull;


public class Menu extends SubjectMenu {

    public boolean playMusic;
    public boolean playFX;
    public boolean vibrate;

    private final Preferences preferences;


    public Menu(Preferences preferences) {
        this.preferences = preferences;
        playMusic = this.preferences.getBoolean(Prefs.MUSIC.label, true);
        playFX = this.preferences.getBoolean(Prefs.SOUND_FX.label, true);
        vibrate = this.preferences.getBoolean(Prefs.VIBRATE.label, true);

    }

    @NotNull
    private TextButton getMusicButton(Skin mySkin) {
        final TextButton music;
        if (playMusic) {
            music = new TextButton("Music: ON ", mySkin);
        } else {
            music = new TextButton("Music: OFF", mySkin);
        }
        music.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if (playMusic) {
                    music.setText("Music: OFF");
                    preferences.putBoolean(Prefs.MUSIC.label, false);
                    Menu.super.notify(MenuEvent.MUSIC_OFF);
                } else {
                    music.setText("Music: ON ");
                    preferences.putBoolean(Prefs.MUSIC.label, true);
                    Menu.super.notify(MenuEvent.MUSIC_ON);
                }
                playMusic = !playMusic;
                preferences.flush();
                return true;
            }
        });
        return music;
    }

    private TextButton getSoundButton(Skin mySkin) {
        final TextButton soundFX;
        if (playFX) {
            soundFX = new TextButton("SoundFX: ON ", mySkin);
        } else {
            soundFX = new TextButton("SoundFX: OFF", mySkin);
        }
        soundFX.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (playFX) {
                    soundFX.setText("SoundFX: OFF");
                    preferences.putBoolean(Prefs.SOUND_FX.label, false);
                } else {
                    soundFX.setText("SoundFX: ON ");
                    preferences.putBoolean(Prefs.SOUND_FX.label, true);
                }
                playFX = !playFX;
                preferences.flush();
                return true;
            }
        });
        return soundFX;
    }

    private TextButton getVibration(Skin mySkin) {
        final TextButton vibration;
        if (vibrate) {
            vibration = new TextButton("Vibration: ON ", mySkin);
        } else {
            vibration = new TextButton("Vibration: OFF", mySkin);
        }
        vibration.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (vibrate) {
                    vibration.setText("Vibration: OFF");
                    preferences.putBoolean(Prefs.VIBRATE.label, false);

                } else {
                    vibration.setText("Vibration: ON ");
                    preferences.putBoolean(Prefs.VIBRATE.label, true);

                }
                vibrate = !vibrate;
                preferences.flush();
                return true;
            }
        });
        return vibration;
    }


    public Window getWindow(Skin mySkin) {
        final ClippedWindow window;

        window = new ClippedWindow("Settings", mySkin, "setting");
        window.getTitleLabel().setAlignment(Align.center);
        window.padTop(BaseMenuScreen.DEFAULT_UNIT * 6.5f);
        window.defaults().pad(BaseMenuScreen.DEFAULT_UNIT);
        TextButton music = getMusicButton(mySkin);
        TextButton soundFX = getSoundButton(mySkin);

        Button close = new TextButton("x", mySkin, "quit");

        close.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                window.setVisible(false);
                return true;
            }
        });
        window.setSize(Gdx.graphics.getWidth() / 1.3f, Gdx.graphics.getHeight() / 1.3f);

        window.setPosition(Gdx.graphics.getWidth() / 2f - window.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - window.getHeight() / 2f);
        window.setMovable(false);
        window.setModal(true);
        window.setVisible(false);
        window.add(music).fill().expand();
        window.add(soundFX).fill().expand().row();
        window.getTitleTable().add(close).padTop(-close.getPrefHeight() / 2).padRight(-close.getPrefWidth() / 2);

        //window.add(close).colspan(2).pad(40).fill();
        //window.setDebug(true);
        return window;
    }


    public Window getWindowGame(Skin mySkin) {
        final ClippedWindow gameWindow;
        gameWindow = new ClippedWindow("Settings", mySkin, "setting");
        gameWindow.getTitleLabel().setAlignment(Align.center);
        gameWindow.padTop(BaseMenuScreen.DEFAULT_UNIT * 6.5f);
        gameWindow.defaults().pad(BaseMenuScreen.DEFAULT_UNIT);
        TextButton music = getMusicButton(mySkin);
        TextButton soundFX = getSoundButton(mySkin);
        TextButton vibrate = getVibration(mySkin);
        Button close = new TextButton("x", mySkin, "close");
        close.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                gameWindow.setVisible(false);
            }
        });

        Button home = new TextButton("Home", mySkin, "quit");
        home.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                gameWindow.setVisible(false);
                Menu.super.notify(MenuEvent.GO_HOME);

            }
        });

        gameWindow.setSize(Gdx.graphics.getWidth() / 1.3f, Gdx.graphics.getHeight() / 1.3f);
        gameWindow.setPosition(Gdx.graphics.getWidth() / 2f - gameWindow.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - gameWindow.getHeight() / 2f);
        gameWindow.setMovable(false);
        gameWindow.setModal(true);
        gameWindow.setVisible(false);
        gameWindow.add(music).grow();
        gameWindow.add(soundFX).grow().row();
        gameWindow.add(vibrate).colspan(2).grow().row();
        gameWindow.add(home).colspan(2).grow();
        gameWindow.getTitleTable().add(close).padTop(-close.getPrefHeight() / 2).padRight(-close.getPrefWidth() / 2);
        // window.setDebug(true);
        return gameWindow;
    }

}
