package com.andrew.game.UI;

import com.andrew.game.enums.MenuBarEvent;
import com.andrew.game.interfaces.MyCallbackMenuBar;
import com.andrew.game.screens.menu.BaseMenuScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MenuBar extends TapCloseDialog {


    public MenuBar(Skin skin, MyCallbackMenuBar myCallBackMenuBar) {
        super("", skin);
        VerticalGroup verticalGroup = new VerticalGroup();
        setBackground(skin.getDrawable("NoTitlePurpleBlue64"));

        TextButton settings = new TextButton("Settings", skin);
        TextButton credits = new TextButton("Credits", skin);
        TextButton account = new TextButton("Account", skin);
        TextButton feedback = new TextButton("Feedback", skin);
        //TextButton exit = new TextButton("Exit", skin, "quit");
        ImageButton imageButton = new ImageButton(skin, "sandwhich");


        verticalGroup.grow();
        verticalGroup.space(BaseMenuScreen.DEFAULT_UNIT);
        verticalGroup.addActor(settings);
        verticalGroup.addActor(credits);
        verticalGroup.addActor(account);
        verticalGroup.addActor(feedback);
        //verticalGroup.addActor(exit);

        //getContentTable().add(imageButton).size(MainMenuScreen.BUTTON_SIZE).right().row();
        getContentTable().pad(BaseMenuScreen.DEFAULT_UNIT * 4);
        getContentTable().add(verticalGroup).grow();
        //getContentTable().add().grow();

        addListener(settings, MenuBarEvent.SETTING, myCallBackMenuBar);
        addListener(credits, MenuBarEvent.CREDIT, myCallBackMenuBar);
        addListener(account, MenuBarEvent.ACCOUNT, myCallBackMenuBar);
        addListener(feedback, MenuBarEvent.FEEDBACK, myCallBackMenuBar);
        //addListener(exit, MenuBarEvent.EXIT, myCallBackMenuBar);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hide();
            }
        });


    }


    @Override
    public float getPrefHeight() {
        return Gdx.graphics.getHeight() * .85f;
    }

    @Override
    public float getPrefWidth() {
        return Gdx.graphics.getWidth() * .3f;
    }

    @Override
    public Dialog show(Stage stage) {
        Dialog dialog = super.show(stage);
        dialog.setPosition(Gdx.graphics.getWidth() - dialog.getPrefWidth(), 0);
        return dialog;
    }

    private void addListener(TextButton textButton, final MenuBarEvent menuBarEvent, final MyCallbackMenuBar myCallBackMenuBar) {
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                hide();

                myCallBackMenuBar.onCallback(menuBarEvent);
            }
        });
    }
}
