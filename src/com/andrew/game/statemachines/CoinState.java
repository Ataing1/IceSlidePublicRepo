package com.andrew.game.statemachines;

import com.andrew.game.currency.Currency;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public interface CoinState {

    void update(Label coinLabel, Currency currency, float delta);

    void flush(Label coinLabel, Currency currency);

}
