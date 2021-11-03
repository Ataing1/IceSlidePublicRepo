package com.andrew.game.statemachines;

import com.andrew.game.player.GameObject;

public interface PlayerState {

    void init(GameObject gameObject);

    void handleInput(GameObject gameObject);

    void update(GameObject gameObject, float delta);

    boolean animateExitFirst(GameObject gameObject, float delta);

    boolean animateExitSecond(GameObject gameObject, float delta);


}

