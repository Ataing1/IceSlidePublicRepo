package com.andrew.game.statemachines;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

public interface CongratulationState {

    Dialog show(Stage stage);

    void update(int trophyEarned, int minSteps, int steps, long startTime);


}
