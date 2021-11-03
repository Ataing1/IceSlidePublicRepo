package com.andrew.game.UI;

import com.andrew.game.EmailValidator;
import com.andrew.game.interfaces.MyCallbackBoolean;
import com.andrew.game.screens.menu.BaseMenuScreen;
import com.andrew.game.slip.Prefs;
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

public class SignInUI extends Dialog {

    Stage stage;

    public SignInUI(final Skin skin, final Slip game) {
        super("", skin);
        addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (x < 0 || x > SignInUI.super.getWidth() || y < 0 || y > SignInUI.super.getHeight()) {
                    Gdx.input.setOnscreenKeyboardVisible(false);
                    stage.unfocusAll();
                    return true;
                }
                return false;
            }
        });

        Label enterEmail = new Label("Enter your email to sign in", new Label.LabelStyle(game.getAssets().buttonLightFont, Color.DARK_GRAY));
        final TextField textField = new TextField(game.getPreferences().getString(Prefs.EMAIL.label, null), skin);
        final TextButton signIn = new TextButton("Sign In", skin, "gold");
        TextButton home = new TextButton("Later", skin);

        enterEmail.setAlignment(Align.center);
        textField.setMessageText("hello@example.com");
        textField.setAlignment(Align.center);

        signIn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setOnscreenKeyboardVisible(false);
                stage.unfocusAll();
                signIn.setDisabled(true);
                //todo email validator shows false negative
                final String email = textField.getText().toLowerCase();
                if (EmailValidator.emailValidator(email)) {
                    game.fireBaseServices.SendEmailSignUp(email, new MyCallbackBoolean() {
                        @Override
                        public void onCallback(boolean status) {
                            if (status) {
                                game.getPreferences().putString(Prefs.EMAIL.label, email);
                                game.getPreferences().flush();
                                new Dialog("", skin) {
                                    @Override
                                    protected void result(Object object) {
                                        signIn.setDisabled(false);
                                    }
                                }.text("  An Email has been sent to your inbox  ").button("  Re-enter email  ").show(stage);
                            }
                        }
                    });
                } else {
                    game.fireBaseServices.toast(textField + " is an invalid email format");
                    signIn.setDisabled(false);
                }

            }
        });
        home.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hide();
                //todo add a "no worries find me in the account tab" dialog or toast
            }
        });

        Table table = getContentTable();
        table.pad(BaseMenuScreen.DEFAULT_UNIT);
        table.defaults().space(BaseMenuScreen.DEFAULT_UNIT).width(enterEmail.getPrefWidth() + 2 * BaseMenuScreen.DEFAULT_UNIT);
        table.add(enterEmail).row();
        table.add(textField).row();
        table.add(signIn).row();
        table.add(home);

    }

    @Override
    public Dialog show(Stage stage) {
        this.stage = stage;
        Dialog dialog = super.show(stage);
        dialog.setPosition(Math.round((stage.getWidth() - getWidth()) / 2), Math.round((stage.getHeight() - getHeight()) / 1.2));
        return dialog;
    }
}
