package com.andrew.game.screens.gameplay.congratulation;

import com.andrew.game.enums.CongratulationCommand;
import com.andrew.game.interfaces.MyCallbackCongratulation;
import com.andrew.game.slip.Slip;
import com.andrew.game.statemachines.CongratulationState;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

public class TutorialCongratulation extends Dialog implements CongratulationState {

    private final Slip game;
    private final MyCallbackCongratulation myCallbackCongratulation;
    private final Skin mySkin;

    public TutorialCongratulation(Slip game, Skin skin, final MyCallbackCongratulation myCallbackCongratulation) {
        super("Tutorial Complete!", skin);
        mySkin = skin;
        this.game = game;
        this.myCallbackCongratulation = myCallbackCongratulation;
        getTitleLabel().setAlignment(Align.center);
        padTop(getTitleLabel().getPrefHeight());
        padBottom(20).padRight(20).padLeft(20);
        getContentTable().defaults().pad(20);
        getButtonTable().defaults().pad(20);
        TextButton continueButton = new TextButton("Continue", mySkin);
        continueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                myCallbackCongratulation.onCallback(CongratulationCommand.GO_HOME);
            }
        });
        getButtonTable().add(continueButton).grow();


    }

    @Override
    public void update(int trophyEarned, int minSteps, int steps, long startTime) {

    }
}
