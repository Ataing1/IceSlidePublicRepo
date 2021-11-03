package com.andrew.game.currency;

import com.andrew.game.statemachines.CoinState;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class SubtractCoinState implements CoinState {
    float currentCoins;
    float amount;
    final int goal;

    public SubtractCoinState(int currentCoins, int amount) {
        this.currentCoins = currentCoins;
        this.amount = amount;
        goal = currentCoins - amount;
    }

    @Override
    public void update(Label coinLabel, Currency currency, float delta) {

        if (amount <= 1) {
            currentCoins = goal;
            coinLabel.setText(String.valueOf((int) currentCoins));
            currency.resetCoinState((int) currentCoins);
        } else {
            currentCoins -= amount * delta;
            coinLabel.setText(String.valueOf((int) currentCoins));
            amount -= amount * delta;
        }
    }

    @Override
    public void flush(Label coinLabel, Currency currency) {
        currentCoins = goal;
        coinLabel.setText(String.valueOf((int) currentCoins));
        currency.resetCoinState((int) currentCoins);
    }
}
