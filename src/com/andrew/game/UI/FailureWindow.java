package com.andrew.game.UI;

import com.andrew.game.enums.AdStatus;
import com.andrew.game.enums.CongratulationCommand;
import com.andrew.game.interfaces.MyCallbackAd;
import com.andrew.game.interfaces.MyCallbackCongratulation;
import com.andrew.game.slip.Slip;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class FailureWindow extends Dialog {

    private boolean extraMovesEarned = false;

    public FailureWindow(int trophyLost, Skin mySkin, final Slip game, final MyCallbackCongratulation myCallbackCongratulation) {
        super("", mySkin);
        Button button = new Button(mySkin);
        TextButton close = new TextButton("Home", mySkin);
        close.getLabel().setColor(Color.BLACK);
        Label label = new Label("Out of moves!", new Label.LabelStyle(game.getAssets().settingsFont, Color.BLACK));
        Label lostTrophies = new Label("-" + trophyLost + " Trophies", new Label.LabelStyle(game.getAssets().settingsFont, Color.BLACK));

        button.add(new Label("Get +5 Moves", new Label.LabelStyle(game.getAssets().settingsFont, Color.BLACK))).row();
        button.add(new Label("watch an ad", new Label.LabelStyle(game.getAssets().textFont, Color.BLACK))).row();

        button.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //play reward ad, on callback call gamescreen method, and close this window
                hide();
                if (game.accountManager.getAccountObject().getAdFree()) {
                    myCallbackCongratulation.onCallback(CongratulationCommand.EXTRA_MOVES);
                } else {
                    game.fireBaseServices.showExtraMovesAd(new MyCallbackAd() {
                        @Override
                        public void onCallback(AdStatus status) {
                            System.out.println("callback from extramoves ad, status" + status);

                            switch (status) {
                                case EARNED_REWARD:
                                    extraMovesEarned = true;
                                    break;
                                case AD_CLOSED:
                                    if (extraMovesEarned) {
                                        myCallbackCongratulation.onCallback(CongratulationCommand.EXTRA_MOVES);
                                    } else {
                                        myCallbackCongratulation.onCallback(CongratulationCommand.GO_HOME);
                                    }

                                    break;
                                case NOT_LOADED:
                                    break;
                            }

                        }
                    });
                }
                return true;
            }
        });

        Button button1 = new Button(mySkin);
        button1.add(new Label("Restart", new Label.LabelStyle(game.getAssets().settingsFont, Color.BLACK))).row();
        button1.add(new Label("lose a life", new Label.LabelStyle(game.getAssets().textFont, Color.BLACK))).row();

        button1.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                hide();
                myCallbackCongratulation.onCallback(CongratulationCommand.RETRY);
                return true;
            }
        });


        close.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                myCallbackCongratulation.onCallback(CongratulationCommand.GO_HOME);
                return true;
            }
        });

        getContentTable().add(label).colspan(2).row();
        getContentTable().add(lostTrophies).colspan(2).row();
        getContentTable().add(button).uniformX();
        getContentTable().add(button1).uniformX().grow();
        getButtonTable().add(close).growX();


    }
}
