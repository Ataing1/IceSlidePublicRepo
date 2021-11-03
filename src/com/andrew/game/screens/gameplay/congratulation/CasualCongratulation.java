package com.andrew.game.screens.gameplay.congratulation;

import com.andrew.game.enums.CongratulationCommand;
import com.andrew.game.interfaces.MyCallbackCongratulation;
import com.andrew.game.slip.Assets;
import com.andrew.game.slip.Slip;
import com.andrew.game.statemachines.CongratulationState;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import java.util.concurrent.ThreadLocalRandom;

public class CasualCongratulation extends Dialog implements CongratulationState {

    private final Slip game;
    private final MyCallbackCongratulation myCallbackCongratulation;
    private final Skin mySkin;

    public CasualCongratulation(Slip game, Skin skin, MyCallbackCongratulation myCallbackCongratulation) {
        super("", skin, "setting");
        getTitleLabel().setAlignment(Align.center);
        mySkin = skin;
        this.game = game;
        this.myCallbackCongratulation = myCallbackCongratulation;
    }

    @Override
    public void update(int screenLevel, int minSteps, int steps, long startTime) {
        layoutUI(minSteps, steps);
    }

    private void layoutUI(int minSteps, int steps) {

        Image coinImage;
        Image coinImage1;
        Table qualityBonus;
        Label qualityBonusLabel;

        int totalCoinsEarnedThisGame;
        int bonusAmount;
        getTitleLabel().setText("Map Completed!");
        padTop(getTitleLabel().getPrefHeight());
        padBottom(20).padRight(20).padLeft(20);
        getContentTable().defaults().pad(20);
        getButtonTable().defaults().pad(20);
        coinImage = new Image(mySkin.getDrawable(Assets.COIN_IMAGE));
        coinImage1 = new Image(mySkin.getDrawable(Assets.COIN_IMAGE));
        bonusAmount = bonusInit(minSteps, steps);
        getContentTable().row();
        if (bonusAmount > 0) {
            qualityBonus = new Table();
            qualityBonusLabel = new Label("Bonus: ", new Label.LabelStyle(game.getAssets().textFont, Color.BLACK));
            Table coinTable = getCoinTable(coinImage, bonusAmount);
            qualityBonus.add(qualityBonusLabel, coinTable);
            getContentTable().add(qualityBonus).colspan(2);
            getContentTable().row();
        }

        totalCoinsEarnedThisGame = calculateCoins(bonusAmount);
        Table totalEarned = new Table();
        Label totalEarnedLabel = new Label("Total earned: ", new Label.LabelStyle(game.getAssets().textFont, Color.BLACK));
        Table coinTable = getCoinTable(coinImage1, totalCoinsEarnedThisGame);
        totalEarned.add(totalEarnedLabel, coinTable);
        getContentTable().add(totalEarned).colspan(2);
        getContentTable().row();
        TextButton home = new TextButton("Home", mySkin);
        TextButton next = new TextButton("Next Level", mySkin);
        home.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                myCallbackCongratulation.onCallback(CongratulationCommand.GO_HOME);
            }
        });
        next.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                myCallbackCongratulation.onCallback(CongratulationCommand.GO_NEXT);
            }
        });
        getButtonTable().add(home);
        getButtonTable().add(next).growX();
        //casualCongratsWindow.setSize(casualCongratsWindow.getPrefWidth(), casualCongratsWindow.getPrefHeight());
        // casualCongratsWindow.setPosition(Gdx.graphics.getWidth() / 2f - casualCongratsWindow.getWidth() / 2f,
        // Gdx.graphics.getHeight() / 2f - casualCongratsWindow.getHeight() / 2f);

    }


    private Table getCoinTable(Image coinImage, int amount) {
        Table coinTable = new Table();
        Label coinLabel = new Label(" " + amount, new Label.LabelStyle(game.getAssets().textFont, Color.BLACK));
        coinTable.add(coinImage).size(game.getAssets().textFont.getCapHeight(), game.getAssets().textFont.getCapHeight());
        coinTable.add(coinLabel);
        return coinTable;
    }

    private int bonusInit(int minSteps, int steps) {
        if (minSteps == steps) {
            getContentTable().add(new Label("Perfect!", new Label.LabelStyle(game.getAssets().textFont, Color.BLACK))).colspan(2);
            return ThreadLocalRandom.current().nextInt(15, 20);
        } else if (minSteps * 1.5 > steps) {
            getContentTable().add(new Label("Fantastic!", new Label.LabelStyle(game.getAssets().textFont, Color.BLACK))).colspan(2);
            return ThreadLocalRandom.current().nextInt(8, 12);
        } else {
            getContentTable().add(new Label("Nice!", new Label.LabelStyle(game.getAssets().textFont, Color.BLACK))).colspan(2);
            return ThreadLocalRandom.current().nextInt(0, 5);
        }

    }

    private int calculateCoins(int bonus) {
        //todo make the reward for harder levels more than easier levels, like the graph of x^2 for al modes
        int random = ThreadLocalRandom.current().nextInt(10, 15);
        int totalEarned = bonus + random;
        game.accountManager.addCoins(totalEarned);
        return totalEarned;
    }


}
