package com.andrew.game.UI;

import com.andrew.game.interfaces.MyCallbackEmpty;
import com.andrew.game.screens.menu.BaseMenuScreen;
import com.andrew.game.screens.menu.MainMenuScreen;
import com.andrew.game.slip.Prefs;
import com.andrew.game.slip.Slip;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

public class GameMode extends TapCloseDialog {

    private static final String CASUAL_STRING = "-No Lives, No Step Count, No Stress\n-Play a random map rated at your skill level or below\n-Default Reward";
    private static final String CHALLENGE_STRING = "-5 lives, Step Count, Ranked\n-Play a random map at your skill level\n-Bigger Reward";
    //private static final String HARDCORE_STRING = "-Not for the faint-hearted\n-Move limit & Time limit\n-Biggest Reward";

    public GameMode(Skin skin, final Slip game, final MyCallbackEmpty myCallbackEmpty) {
        super("", skin, "test");

        Table tableCasual, tableChallenge, tableHardcore;

        tableCasual = new Table();
        tableChallenge = new Table();
        //tableHardcore = new Table();

        //tableHardcore.addAction(Actions.hide());

        tableCasual.setBackground(skin.getDrawable("NoTitleWindow"));
        tableChallenge.setBackground(skin.getDrawable("NoTitleWindow"));
        //tableHardcore.setBackground(skin.getDrawable("NoTitleWindow"));

        Label titleCasual, titleChallenge, titleHardcore;
        titleCasual = new Label("Casual", new Label.LabelStyle(game.getAssets().settingsFont, Color.WHITE));
        titleChallenge = new Label("Challenge", new Label.LabelStyle(game.getAssets().settingsFont, Color.WHITE));
        //titleHardcore = new Label("Hardcore", new Label.LabelStyle(game.getAssets().settingsFont, Color.WHITE));

        Label descCasual, descChallenge, descHardcore;
        descCasual = new Label(CASUAL_STRING, skin, "text");
        descCasual.setWrap(true);
        descCasual.setAlignment(Align.center);
        descChallenge = new Label(CHALLENGE_STRING, skin, "text");
        descChallenge.setWrap(true);
        descChallenge.setAlignment(Align.center);
        //descHardcore = new Label(HARDCORE_STRING, skin, "text");
        //descHardcore.setWrap(true);
        // descHardcore.setAlignment(Align.center);


        TextButton selectCasual, selectChallenge, selectHardcore;
        selectCasual = new TextButton("Select", skin);
        selectChallenge = new TextButton("Select", skin);
        //selectHardcore = new TextButton("Select", skin);

        tableCasual.add(titleCasual).row();
        tableCasual.add(descCasual).grow().row();
        tableCasual.add(selectCasual);

        tableChallenge.add(titleChallenge).row();
        tableChallenge.add(descChallenge).grow().row();
        tableChallenge.add(selectChallenge);

        // tableHardcore.add(titleHardcore).row();
        //tableHardcore.add(descHardcore).grow().row();
        // tableHardcore.add(selectHardcore);

        Table container = new Table();
        container.defaults().space(BaseMenuScreen.DEFAULT_UNIT * 5);
        container.defaults().width(Gdx.graphics.getWidth() * .5f).growY();
        //container.add(tableCasual, tableChallenge, tableHardcore);
        container.add(tableCasual, tableChallenge);

        ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.setScrollingDisabled(false, true);
        getContentTable().add(scrollPane);
        //getContentTable().setDebug(true, true);

        selectCasual.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getPreferences().putString(Prefs.GAME_MODE.label, MainMenuScreen.CASUAL);
                game.getPreferences().flush();
                myCallbackEmpty.onCallback();
                hide();

            }
        });

        selectChallenge.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getPreferences().putString(Prefs.GAME_MODE.label, MainMenuScreen.CHALLENGE);
                game.getPreferences().flush();
                myCallbackEmpty.onCallback();
                hide();
            }
        });

       /* selectHardcore.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getPreferences().putString(Prefs.GAME_MODE.label, MainMenuScreen.HARDCORE);
                game.getPreferences().flush();
                myCallbackEmpty.onCallback();
                hide();
            }
        });*/
    }

    @Override
    public float getPrefWidth() {
        return Gdx.graphics.getWidth() * .6f;
    }

    /*@Override
    public float getPrefHeight() {
        return Gdx.graphics.getHeight();
    }

     */
}
