package com.andrew.game.UI;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * extends dialog
 * Configured for user interaction with Keyboard, by taking up the top half of the screen only
 *
 * @see Dialog overrides getPrefWidth and getPrefHeight to control width adn height
 */
public class AccountDialog extends Dialog {

    // private final float width;
    //private final float height;

    public AccountDialog(String title, Skin skin) {
        super(title, skin);
        top();
        defaults().top();
        getContentTable().defaults().top();
        getButtonTable().defaults().top();
        getContentTable().top();
        getButtonTable().top();
        // getTitleLabel().setAlignment(Align.center);
        //height = Gdx.graphics.getHeight() * .5f;
        //width = Gdx.graphics.getWidth();
        setFillParent(true);

    }

   /* @Override
    public float getPrefWidth() {
        return width;
    }
*/
  /*  @Override
    public float getPrefHeight() {
        return height;
    }*/

    @Override
    public Dialog show(Stage stage) {
        Dialog dialog = super.show(stage);
        //dialog.setPosition(0,Gdx.graphics.getHeight() - getPrefHeight() );
        return dialog;
    }
}
