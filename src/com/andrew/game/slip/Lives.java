package com.andrew.game.slip;

import com.andrew.game.AccountObject;
import com.andrew.game.screens.menu.BaseMenuScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Date;
import java.util.Locale;


public class Lives {

    private Label lifeLabel;
    private TextButton lifeCount;
    private long lastLostLifeTime;

    private int lives;
    private final Slip game;
    private final long min30 = 1800000;

    //todo only take a life away when the user loses or quits game
    public Lives(AccountObject accountObject, Slip game) {
        this.game = game;
        lives = accountObject.getLives();
        if (lives < 0) {
            lives = 0;
        }
        Date date = accountObject.getTimeTillNextLife();
        if (date == null) { //in case date is null
            lastLostLifeTime = 0;
        } else { //else because date can't be null in the following conditionals
            if (lives == 5) {
                lastLostLifeTime = 0;
            } else {
                int livesEarnedWhileAway = (int) Math.min(TimeUtils.timeSinceMillis(date.getTime()) / min30, 5);
                lives = Math.min(livesEarnedWhileAway + lives, 5);
                if (lives < 5) {
                    lastLostLifeTime = date.getTime() + livesEarnedWhileAway * min30;
                }
            }
        }
        getLives();
    }

    public void spendLife() {
        lives--;
        if (lives < 0) {
            lives = 0; //this case happens when the user plays a challenge game with 1 life remaining,
            // then he skips the game. on the other side of the ad, the game takes another life, which would make it negative
            //making it 0 rewards the user for watching an ad, not punishing him, by giving him -1 lives
        }
        updateLabel();
        if (lastLostLifeTime == 0) {
            lastLostLifeTime = TimeUtils.millis();
        }
        game.accountManager.getAccountObject().setLives(lives);
        game.accountManager.updateLifeDate(getLastLostLifeTime());

    }

    public int getLives() {
        if (lives < 2 && !game.accountManager.getAccountObject().getAdFree()) {
            game.fireBaseServices.loadExtraLivesAd();
        }
        return lives;
    }

    public void addLives(int lives) {
        this.lives += lives;
        game.accountManager.getAccountObject().setLives(this.lives);
        game.accountManager.updateAccountObject("lives");
        updateLabel();
    }

    private void updateLabel() {
        lifeCount.setText(String.valueOf(lives));
    }

    private Date getLastLostLifeTime() {
        Date date = new Date();
        date.setTime(lastLostLifeTime);
        return date;
    }

    /**
     * must reflect most recently gotten life,
     * or else calculations for time elapsed while offline will be incorrect
     * for example, the user has 2 lives, plays for 30 mins, and now has 3 lives.
     * if remaining time is not updated, the time when the user had 2 lives will be saved to the database
     * then, when the user logs back on, 10 minutes after getting to 3 lives, the game sees that the last life lost was
     * 40 minutes ago and the amount of lives the user currently has is 3.
     * The constructor calculates lives gained offline by [(time since last life)/30 minutes], so it will give the user
     * 1 more life, so 3 + 1 = 4. which is wrong. the user should only have three lives
     */
    public void update() {
        if (lives < 5) {
            if (TimeUtils.timeSinceMillis(lastLostLifeTime) >= min30) { //if time elapsed is more than 30 minutes
                addLives(1);
                if (lives == 5) {
                    lastLostLifeTime = 0;
                    lifeLabel.setText("Full");
                } else {
                    lastLostLifeTime = TimeUtils.millis();
                }
                game.accountManager.updateLifeDate(getLastLostLifeTime());
            } else {

                lifeLabel.setText(String.format(Locale.US,
                        "%02d:%02d",
                        (min30 - TimeUtils.timeSinceMillis(lastLostLifeTime)) / (1000 * 60),
                        ((min30 - TimeUtils.timeSinceMillis(lastLostLifeTime)) / 1000) % 60));
            }
        }

    }


    public Table getLivesTable(Skin mySkin, Assets assets) {
        if (lifeLabel == null) {
            lifeLabel = new Label("full", new Label.LabelStyle(assets.buttonLightFont, Color.WHITE));
        }
        lifeCount = getLifeCountButton(mySkin);
        lifeLabel.setAlignment(Align.center);
        lifeLabel.setWrap(false);

        Table lifeTable = new Table();
        lifeTable.defaults().space(BaseMenuScreen.DEFAULT_UNIT);
        lifeTable.left();

        lifeTable.add(lifeCount).size(BaseMenuScreen.BUTTON_SIZE);
        Container<Label> container = new Container<>(lifeLabel);
        container.setBackground(mySkin.getDrawable("NoTitlePurpleBlue64"));
        lifeTable.add(container).width(82.78095238f * Gdx.graphics.getDensity());
        return lifeTable;
    }

    public TextButton getLifeCountButton(Skin mySkin) {
        return new TextButton(String.valueOf(lives), mySkin, "lifes");
    }

}
