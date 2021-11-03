package com.andrew.game.UI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ClippedDialog extends Dialog {

    public ClippedDialog(String title, Skin skin) {
        super(title, skin);

    }

    public ClippedDialog(String title, Skin skin, String styleName) {
        super(title, skin, styleName);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setClip(true);
        super.draw(batch, parentAlpha);
        setClip(false);
    }
}
