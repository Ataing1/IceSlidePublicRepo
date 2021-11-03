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
import com.badlogic.gdx.utils.TimeUtils;

import java.util.concurrent.ThreadLocalRandom;

public class ChallengeCongratulation extends Dialog implements CongratulationState {
    private final Slip game;
    private final MyCallbackCongratulation myCallbackCongratulation;
    private final Skin mySkin;

    public ChallengeCongratulation(Slip game, Skin mySkin, MyCallbackCongratulation myCallbackCongratulation) {
        super("", mySkin, "setting");
        getTitleLabel().setAlignment(Align.center);
        this.mySkin = mySkin;
        this.game = game;
        this.myCallbackCongratulation = myCallbackCongratulation;
    }

    @Override
    public void update(int trophyEarned, int minSteps, int steps, long startTime) {

        layoutUI(trophyEarned, minSteps, startTime);

    }


    private void layoutUI(int trophyEarned, int minSteps, long startTime) {
        long elapsedTime = TimeUtils.timeSinceMillis(startTime);
        Label trophyEarnedLabel;
        Label timeLabel;
        Table timeBonusTable;
        Label timeBonusLabel;
        Image coinImage;
        Image coinImage1;

        int totalCoinsEarnedThisGame;
        int timeBonusAmount;

        getTitleLabel().setText("Challenge Completed!");
        padTop(getTitleLabel().getPrefHeight());
        padBottom(20).padRight(20).padLeft(20);
        getContentTable().defaults().pad(20);
        getButtonTable().defaults().pad(20);

        trophyEarnedLabel = new Label("+" + trophyEarned + " Trophies", new Label.LabelStyle(game.getAssets().textFont, Color.BLACK));
        getContentTable().add(trophyEarnedLabel).row();
        timeLabel = new Label("Time: " + getTimeString(elapsedTime), new Label.LabelStyle(game.getAssets().textFont, Color.BLACK));
        getContentTable().add(timeLabel).row();
        timeBonusAmount = calculateTimeBonus(elapsedTime, minSteps);
        coinImage = new Image(mySkin.getDrawable(Assets.COIN_IMAGE));
        coinImage1 = new Image(mySkin.getDrawable(Assets.COIN_IMAGE));
        if (timeBonusAmount > 0) {
            timeBonusTable = new Table();
            timeBonusLabel = new Label("Time bonus: ", new Label.LabelStyle(game.getAssets().textFont, Color.BLACK));
            //Table coinTable = getCoinTable(coinImage, timeBonusAmount);
            timeBonusTable.add(timeBonusLabel);
            timeBonusTable.add(getCoinTable(coinImage, timeBonusAmount));
            getContentTable().add(timeBonusTable).colspan(1).row();
        }
        totalCoinsEarnedThisGame = calculateCoins(timeBonusAmount);


        Table totalEarned = new Table();
        Label totalEarnedLabel = new Label("Total earned: ", new Label.LabelStyle(game.getAssets().textFont, Color.BLACK));
        Table coinTable = getCoinTable(coinImage1, totalCoinsEarnedThisGame);
        totalEarned.add(totalEarnedLabel, coinTable);
        getContentTable().add(totalEarned).colspan(1);
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
    }


    private String getTimeString(long timeSinceMillis) {
        long minutes = (timeSinceMillis / 1000) / 60;
        long seconds = (timeSinceMillis / 1000) % 60;
        return minutes + " Min " + seconds + " Sec";
    }

    private int calculateTimeBonus(long elapsedTime, int minSteps) {
        long maxTime = (long) (minSteps * 1.5 * 5_000); //10_000 is 10 seconds 10000milliseconds
        int secondsRemaining = (int) ((maxTime - elapsedTime) / 1000);
        return secondsRemaining / 4; //for every 4 seconds remaining, you get extra coins
    }

    private Table getCoinTable(Image coinImage, int amount) {
        Table coinTable = new Table();
        Label coinLabel = new Label(" " + amount, new Label.LabelStyle(game.getAssets().textFont, Color.BLACK));
        coinTable.add(coinImage).size(game.getAssets().textFont.getCapHeight(), game.getAssets().textFont.getCapHeight());
        coinTable.add(coinLabel);
        return coinTable;
    }

    private int calculateCoins(int bonus) {
        //todo make the reward for harder levels more than easier levels, like the graph of x^2 for al modes
        int random = ThreadLocalRandom.current().nextInt(20, 35);
        int totalEarned = bonus + random;
        game.accountManager.addCoins(totalEarned);
        System.out.println("adding coins from challenge congratulation");
        return totalEarned;
    }
}
