package com.andrew.game.UI;

import com.andrew.game.interfaces.MyCallbackBoolean;
import com.andrew.game.interfaces.MyCallbackEmpty;
import com.andrew.game.screens.menu.BaseMenuScreen;
import com.andrew.game.slip.Slip;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

public class EnterUsernameDialog extends Dialog {

    Slip game;

    Stage stage;

    public EnterUsernameDialog(Skin skin, final Stage stage, final Slip game, final MyCallbackEmpty myCallbackEmpty) {
        super("", skin);
        this.game = game;

        addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (x < 0 || x > EnterUsernameDialog.super.getWidth() || y < 0 || y > EnterUsernameDialog.super.getHeight()) {
                    Gdx.input.setOnscreenKeyboardVisible(false);
                    stage.unfocusAll();
                    return true;
                }
                return false;
            }
        });

        Label label = new Label("Welcome! Please enter your display name", new Label.LabelStyle(game.getAssets().buttonLightFont, Color.WHITE));
        final TextField passwordBox = new TextField("", skin);
        final TextButton next = new TextButton("Submit", skin, "gold");

        label.setAlignment(Align.center);
        passwordBox.setMessageText("User12345");
        passwordBox.setAlignment(Align.center);
        next.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setOnscreenKeyboardVisible(false);
                stage.unfocusAll();
                next.setDisabled(true);
                verifyUsername(passwordBox.getText(), new MyCallbackBoolean() {
                    @Override
                    public void onCallback(boolean status) {
                        if (status) {
                            game.accountManager.getAccountObject().setUsername(passwordBox.getText());
                            game.accountManager.updateAccountObject("username");
                            hide();
                            myCallbackEmpty.onCallback();
                        } else {
                            next.setDisabled(false);
                        }
                    }
                });
            }
        });

        Table contentTable = getContentTable();
        contentTable.pad(BaseMenuScreen.DEFAULT_UNIT);
        contentTable.defaults().width(label.getPrefWidth() + BaseMenuScreen.DEFAULT_UNIT * 2).space(BaseMenuScreen.DEFAULT_UNIT);
        contentTable.add(label).row();
        contentTable.add(passwordBox).row();
        contentTable.add(next);

    }

    private void verifyUsername(String username, final MyCallbackBoolean myCallbackBoolean) {
        if (username.replace(" ", "").equals("")) {
            game.fireBaseServices.toast("enter a username");
            myCallbackBoolean.onCallback(false);
            return;
        }
        game.fireBaseServices.usernameExists(username, new MyCallbackBoolean() {
            @Override
            public void onCallback(boolean status) {
                System.out.println("called back");
                if (status) {
                    Gdx.app.postRunnable(new Runnable() {

                        @Override
                        public void run() {
                            game.fireBaseServices.toast("Username Saved");

                            myCallbackBoolean.onCallback(true);
                        }
                    });
                } else {
                    game.fireBaseServices.toast("Username taken!");
                    myCallbackBoolean.onCallback(false);
                }

            }
        });
    }

    @Override
    public Dialog show(Stage stage) {
        //this.stage = stage;
        Dialog dialog = super.show(stage);
        dialog.setPosition(Math.round((stage.getWidth() - getWidth()) / 2), Math.round((stage.getHeight() - getHeight()) / 1.5));
        return dialog;
    }
}
