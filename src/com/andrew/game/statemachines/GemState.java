package com.andrew.game.statemachines;

import com.andrew.game.currency.Currency;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public interface GemState {

    void update(Label gemLabel, Currency currency, float delta);
}
