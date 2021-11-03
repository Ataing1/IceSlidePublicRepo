package com.andrew.game.slip;

import com.andrew.game.AccountObject;
import com.andrew.game.currency.Currency;
import com.andrew.game.screens.menu.BaseMenuScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Keeps AccountObject in Launcher synchronized with Core by updating Launcher AccountObject each time there is a change in Core AccountLauncher
 */
public class AccountManager {

    private final Slip game;
    private AccountObject accountObject;
    private Currency currency;
    private Lives lives;
    private Trophy trophy;



    public AccountManager(Slip game) {
        this.game = game;
    }

    public void setAccountObject(AccountObject accountObject) {
        this.accountObject = accountObject;
        currency = new Currency(accountObject.getCoins());
        lives = new Lives(accountObject, game);
        trophy = new Trophy(accountObject, game);
    }


    public AccountObject getAccountObject() {
        return accountObject;
    }

    public void updateAccountObject(String updatedItem) {
        Gdx.app.log("firestore", "updating " + updatedItem);
        game.fireBaseServices.updateAccountObject(accountObject);
    }

    public void unlockSprite(int spriteID, int amount) {
        accountObject.getSprites().add(spriteID);
        game.fireBaseAnalytics.logSpendVirtualCurrency("sprite", amount, "coins");
        subCoins(amount);
        updateAccountObject("sprites");
    }

    public void setAdFree(boolean adFree) {
        accountObject.setAdFree(adFree);
        updateAccountObject("Ad Free");
    }

    public void addCoins(int amount) {
        System.out.println("adding " + amount + " coins");
        if (accountObject.getCoins() + amount >= 100_000) {
            amount = 100_000 - accountObject.getCoins();
            game.fireBaseServices.toast("Max Coins Reached");
        }
        accountObject.addCoins(amount);
        currency.addCoins(amount);
        updateAccountObject("coins");
        game.fireBaseAnalytics.logEarnVirtualCurrency(amount, "coins");
    }

    public void subCoins(int amount) {
        accountObject.subCoins(amount);
        currency.subCoins(amount);
        updateAccountObject("coins");
        // game.fireBaseAnalytics.logEarnVirtualCurrency(amount, "coins");
    }

    /* public void addGems(int amount) {
         accountObject.addGems(amount);
         currency.addGems(amount);
         updateAccountObject("gems");
         game.fireBaseAnalytics.logEarnVirtualCurrency(amount, "gems");
     }

     public void subGems(int amount) {
         accountObject.subGems(amount);
         currency.subGems(amount);
         updateAccountObject("gems");
         // game.fireBaseAnalytics.logEarnVirtualCurrency(amount, "gems");
     }
 */
    public void updateLifeDate(Date date) {
        accountObject.setTimeTillNewLife(date);
        updateAccountObject("timeTillNewLife");
    }

    public int registerWin() {
        int currentTrophy = accountObject.getTrophy();
        int addTrophy;
        if (currentTrophy < 500) {
            addTrophy = ThreadLocalRandom.current().nextInt(30, 60);
        } else if (currentTrophy < 1000) {
            addTrophy = ThreadLocalRandom.current().nextInt(20, 40);
        } else {
            addTrophy = ThreadLocalRandom.current().nextInt(25, 31);
        }
        accountObject.setTrophy(currentTrophy + addTrophy);
        updateAccountObject("Trophy");
        return addTrophy;
    }

    public int registerLoss() {
        int currentTrophy = accountObject.getTrophy();
        int subTrophy;
        if (currentTrophy < 500) {
            subTrophy = ThreadLocalRandom.current().nextInt(5, 15);
        } else if (currentTrophy < 1000) {
            subTrophy = ThreadLocalRandom.current().nextInt(10, 20);
        } else {
            subTrophy = ThreadLocalRandom.current().nextInt(15, 25);
        }
        accountObject.setTrophy(currentTrophy - subTrophy);

        updateAccountObject("Trophy");
        return subTrophy;
    }


    @Override
    public String toString() {
        return accountObject.toString();
    }

    public void receivedDailyReward() {
        accountObject.setDailyRewardDate(new Date());
        accountObject.setStreak(accountObject.getStreak() + 1);
        updateAccountObject("Daily reward");

    }

    public Currency getCurrencyObject() {
        return currency;
    }

    public Lives getLivesObject() {
        return lives;
    }

    public Trophy getTrophyObject() {
        return trophy;
    }

    public Table getTable(Skin skin, Assets assets) {
        Table table = new Table();
        table.pad(BaseMenuScreen.DEFAULT_UNIT);
        table.defaults().space(BaseMenuScreen.DEFAULT_UNIT).align(Align.left);
        table.add(currency.getCurrencyTable(skin, assets)).growX().row();
        table.add(lives.getLivesTable(skin, assets)).growX().row();
        table.add(trophy.getTrophyTable(skin, assets)).growX().row();

        table.validate();
        table.setPosition(table.getPrefWidth() / 2f, Gdx.graphics.getHeight() - table.getPrefHeight() / 2f);
        return table;
    }


    public void update(float delta) {
        currency.act(delta);
        lives.update();
        trophy.update();


    }


}
