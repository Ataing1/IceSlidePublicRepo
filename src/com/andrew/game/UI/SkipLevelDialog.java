package com.andrew.game.UI;

import com.andrew.game.enums.AdStatus;
import com.andrew.game.interfaces.MyCallbackAd;
import com.andrew.game.interfaces.MyCallbackBoolean;
import com.andrew.game.slip.Slip;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class SkipLevelDialog extends TapCloseDialog {


    public SkipLevelDialog(final Slip game, Skin skin, final MyCallbackBoolean myCallbackBoolean) {
        super("", skin);
        text("Watch ad to skip level?");
        TextButton yes = new TextButton("Skip!", skin);
        yes.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("clicked");
                if (game.accountManager.getAccountObject().getAdFree()) {
                    myCallbackBoolean.onCallback(true);
                } else {
                    game.fireBaseServices.showSkipLevelAd(new MyCallbackAd() {
                        @Override
                        public void onCallback(AdStatus status) {
                            System.out.println("calledBack from skiplevel ad, status: " + status);
                            switch (status) {
                                case EARNED_REWARD:
                                    myCallbackBoolean.onCallback(true);
                                    break;
                                case AD_CLOSED:
                                    hide();
                            }
                        }
                    });
                }

            }
        });
        button(yes);
    }
}
