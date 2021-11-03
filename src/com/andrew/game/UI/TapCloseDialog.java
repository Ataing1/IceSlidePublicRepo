package com.andrew.game.UI;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

public class TapCloseDialog extends Dialog {

    public TapCloseDialog(String title, Skin skin) {
        super(title, skin);
        getTitleLabel().setAlignment(Align.center);
        addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (x < 0 || x > TapCloseDialog.super.getWidth() || y < 0 || y > TapCloseDialog.super.getHeight()) {
                    TapCloseDialog.super.hide();
                    return true;
                }
                return false;
            }
        });

    }

    public TapCloseDialog(String title, Skin skin, String style) {
        super(title, skin, style);
        getTitleLabel().setAlignment(Align.center);
        addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (x < 0 || x > TapCloseDialog.super.getWidth() || y < 0 || y > TapCloseDialog.super.getHeight()) {
                    TapCloseDialog.super.hide();
                    return true;
                }
                return false;
            }
        });

    }

}
