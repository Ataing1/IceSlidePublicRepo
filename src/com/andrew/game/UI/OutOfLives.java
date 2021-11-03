package com.andrew.game.UI;

import com.andrew.game.enums.AdStatus;
import com.andrew.game.interfaces.MyCallbackAd;
import com.andrew.game.interfaces.MyCallbackEmpty;
import com.andrew.game.slip.Slip;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class OutOfLives extends Dialog {

    public OutOfLives(Skin mySkin, final Slip game, final MyCallbackEmpty myCallbackEmpty) {
        super("", mySkin);

        Button button = new Button(mySkin);
        TextButton close = new TextButton("Close", mySkin);
        close.getLabel().setColor(Color.BLACK);
        Label label = new Label("Out of Lives", new Label.LabelStyle(game.getAssets().settingsFont, Color.BLACK));

        button.add(new Label("Get +3 lives ", new Label.LabelStyle(game.getAssets().settingsFont, Color.BLACK))).row();
        button.add(new Label("watch an ad", new Label.LabelStyle(game.getAssets().textFont, Color.BLACK))).row();

        button.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //play reward ad, on callback call gamescreen method, and close this window
                if (game.accountManager.getAccountObject().getAdFree()) {
                    game.accountManager.getLivesObject().addLives(3);
                    hide();
                    myCallbackEmpty.onCallback();
                } else {
                    game.fireBaseServices.showExtraLivesAd(new MyCallbackAd() {
                        @Override
                        public void onCallback(AdStatus status) {
                            System.out.println("callback from extra Lives ad, status" + status);
                            switch (status) {
                                case EARNED_REWARD:
                                    game.accountManager.getLivesObject().addLives(3);
                                    break;
                                case AD_CLOSED:
                                    hide();
                                    myCallbackEmpty.onCallback();

                            }
                        }
                    });
                }

                return true;
            }
        });

        close.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                hide();
                myCallbackEmpty.onCallback();
                return true;
            }
        });

        getContentTable().add(label).colspan(2).row();
        getContentTable().add(button);
        getButtonTable().add(close).growX();


    }


}
