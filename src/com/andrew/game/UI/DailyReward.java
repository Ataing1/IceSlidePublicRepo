package com.andrew.game.UI;

import com.andrew.game.interfaces.MyCallbackBoolean;
import com.andrew.game.slip.Slip;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DailyReward {

    private final Slip game;
    final Skin skin;
    ImageButton presentButton;
    TextButton openButton;
    Label streakLabel;
    private boolean ready;
    private boolean updateTimer;
    private long remainingTime;

    protected final Label countDownTimer;
    final MyCallbackBoolean myCallbackBoolean;

    public DailyReward(Slip game, Skin skin, final MyCallbackBoolean myCallbackBoolean) {
        this.game = game;
        this.skin = skin;
        this.myCallbackBoolean = myCallbackBoolean;
        presentButton = getPresentButton();
        countDownTimer = new Label("", new Label.LabelStyle(game.getAssets().buttonLightFont, Color.WHITE));
        countDownTimer.setAlignment(Align.center);
        openButton = new TextButton("", skin);
        openButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                myCallbackBoolean.onCallback(ready);
            }
        });
        //recalculates streak depending on daily decision
        game.accountManager.getAccountObject().setStreak(
                validate() != 1
                        ? game.accountManager.getAccountObject().getStreak()
                        : 0
        );
        streakLabel = new Label("Days in a row: " + game.accountManager.getAccountObject().getStreak(), new Label.LabelStyle(game.getAssets().textFont, Color.DARK_GRAY));
    }

    public int validate() {
        Date date = game.accountManager.getAccountObject().getDailyRewardDate();
        int dailyDecision = game.fireBaseServices.checkDaily(date);
        System.out.println("daily decision " + dailyDecision);
        if (dailyDecision == 0 || dailyDecision == 1) {
            countDownTimer.setText("Open Now");
            updateTimer = false;
            openButton.setText("Open");
            ready = true;
            //presentButton = getPresentButton(true);
        } else {
            updateTimer = true;
            remainingTime = (date.getTime() + 20 * 60 * 60 * 1000) - (new Date().getTime());
            openButton.setText("Locked");
            ready = false;
            //presentButton = getPresentButton(false);
        }
        return dailyDecision;
    }

    public String getTimeRemaining() {
        return String.format(Locale.US, "%02d hours %02d minutes",
                TimeUnit.MILLISECONDS.toHours(remainingTime),
                TimeUnit.MILLISECONDS.toMinutes(remainingTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainingTime)));
    }


    public void redeemedDailyReward() {
        game.accountManager.receivedDailyReward();
        streakLabel.setText("Days in a row: " + game.accountManager.getAccountObject().getStreak());
        validate();
    }

    public void update(float delta) {
        if (!updateTimer) return;
        remainingTime -= delta * 1000;
        if (remainingTime <= 0) {
            countDownTimer.setText("Open Now");
            //presentButton = getPresentButton(true);
            openButton.setText("Open");
            ready = true;
            updateTimer = false;
        } else {
            countDownTimer.setText(String.format(Locale.US, "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(remainingTime),
                    TimeUnit.MILLISECONDS.toMinutes(remainingTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainingTime)), // The change is in this line
                    TimeUnit.MILLISECONDS.toSeconds(remainingTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingTime))));
        }

    }

    private ImageButton getPresentButton() {
        ImageButton imageButton = new ImageButton(skin, "present");
        imageButton.getImageCell().grow();
        return imageButton;
    }


    public Table getDailyRewardTable() {
        Table table = new Table();
        table.add(streakLabel).row();

        Table presentTimerTable = new Table();
        presentTimerTable.defaults().grow();
        presentTimerTable.add(presentButton).size(110 * Gdx.graphics.getDensity()).row();
        presentTimerTable.add(countDownTimer);

        table.add(presentTimerTable).grow().row();
        table.add(openButton).growX();
        return table;
    }


}
