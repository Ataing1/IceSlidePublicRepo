package com.andrew.game.UI;

import com.andrew.game.interfaces.MyCallbackEmpty;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

public class TapAnyWhereCloseDialog extends Dialog {

    public TapAnyWhereCloseDialog(String title, Skin skin, String style) {
        super(title, skin, style);
        getTitleLabel().setAlignment(Align.center);
        addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                hide();
                return true;
            }
        });

    }

    public TapAnyWhereCloseDialog(String title, Skin skin, String style, final MyCallbackEmpty myCallbackEmpty) {
        super(title, skin, style);
        getTitleLabel().setAlignment(Align.center);
        addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                hide();
                myCallbackEmpty.onCallback();
                return true;
            }
        });

    }

    public TapAnyWhereCloseDialog(String title, Skin skin) {
        super(title, skin);
        getTitleLabel().setAlignment(Align.center);
        addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                hide();
                return true;
            }
        });

    }

}
