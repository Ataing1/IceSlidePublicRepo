package com.andrew.game.screens.gameplay;

import com.andrew.game.MazeJson;
import com.andrew.game.UI.SkipLevelDialog;
import com.andrew.game.enums.AdStatus;
import com.andrew.game.enums.CongratulationCommand;
import com.andrew.game.interfaces.MyCallbackAd;
import com.andrew.game.interfaces.MyCallbackBoolean;
import com.andrew.game.interfaces.MyCallbackCongratulation;
import com.andrew.game.screens.gameplay.congratulation.CasualCongratulation;
import com.andrew.game.slip.Prefs;
import com.andrew.game.slip.Slip;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;

import java.util.concurrent.ThreadLocalRandom;

public class CasualScreen extends BaseGame {
    Table sideBarTable;

    public CasualScreen(Slip game, int indexLevel) {
        super(game, indexLevel);

    }

    @Override
    public MazeJson getMazeJson(int indexLevel) {
        int i;
        //don't need to store casual map number, just play whatever
        i = ThreadLocalRandom.current().nextInt(Math.max(indexLevel - 200, 0), indexLevel - 100);
        System.out.println("overridden Json Called # " + i);
        return new Json().fromJson(MazeJson.class, Gdx.files.internal("mazes/level" + i + ".json"));
    }

    private void congratulationInit(Slip game) {
        congratulationState = new CasualCongratulation(game, mySkin, new MyCallbackCongratulation() {
            @Override
            public void onCallback(CongratulationCommand congratulationCommand) {
                switch (congratulationCommand) {
                    case GO_NEXT:
                        goNextLevel();
                        break;
                    case GO_HOME:
                        goHome();
                        break;
                }
            }
        });
    }

    @Override
    public void show() {
        super.show(); //must be called first becuase it initializes skin
        setSideBarTable();
        congratulationInit(game);

    }

    private void setSideBarTable() {
        //Label levelLabel = new Label("Level\n" + (indexLevel + 1), new Label.LabelStyle(assets.buttonFont, Color.WHITE));
        //levelLabel.setAlignment(Align.center);
        Button skipLevel = new TextButton("Skip", mySkin);
        skipLevel.align(Align.center);
        skipLevel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new SkipLevelDialog(game, mySkin, new MyCallbackBoolean() {
                    @Override
                    public void onCallback(boolean status) {
                        if (status) {
                            game.getPreferences().putInteger(Prefs.CASUAL_SEED.label, -1);
                            game.getPreferences().flush();
                            goNextLevel();
                        }
                    }
                }).show(stage);
            }
        });

        sideBarTable = new Table();  //overall sideTable
        VerticalGroup infoBar = new VerticalGroup();
        infoBar.grow();
        infoBar.align(Align.center);
        // infoBar.addActor(levelLabel);
        infoBar.addActor(skipLevel);

        VerticalGroup buttonBar = new VerticalGroup();
        buttonBar.bottom().grow();
        buttonBar.align(Align.center);
        buttonBar.addActor(settings);
        //todo maybe add reset button
        sideBarTable.add(infoBar).padTop(Gdx.graphics.getHeight() * .05f).row();
        sideBarTable.add().grow().row();
        sideBarTable.add(buttonBar).padBottom(Gdx.graphics.getHeight() * .05f);
        sideBarTable.setSize(sideBarTable.getPrefWidth(), Gdx.graphics.getHeight());
        stage.addActor(sideBarTable);
        //sideBarTable.setDebug(true, true);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        sideBarTable.setSize(sideBarTable.getPrefWidth(), Gdx.graphics.getHeight());
        sideBarTable.setPosition((sideBarWidth - sideBarTable.getPrefWidth()) / 2f, 0);
    }


    @Override
    protected void exitAnimationFinishedEvent() {
        //only show ad if manager is ready and user is not Ad-free
        if (game.interstitialManager.updateAndShow() && !game.accountManager.getAccountObject().getAdFree()) {
            game.fireBaseServices.showInterstitialAd(new MyCallbackAd() {
                @Override
                public void onCallback(AdStatus status) {
                    if (status.equals(AdStatus.AD_CLOSED))
                        onAnimationFinish();
                }
            });
        } else {
            onAnimationFinish();
        }
        super.exitAnimationFinishedEvent();
    }

    private void onAnimationFinish() {
        congratulationState.show(stage);
        sideBarTable.setVisible(false);
        accountTable.setVisible(true);
        accountTable.toFront();
    }


    private void goNextLevel() {
        renderState = RenderStates.EXITING_GAME;
        SequenceAction sequenceAction = new SequenceAction();
        sequenceAction.addAction(Actions.fadeOut(1f));
        sequenceAction.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                game.setScreen(new CasualScreen(game, game.accountManager.getAccountObject().getTrophy()));
                dispose();
            }
        }));
        stage.addAction(sequenceAction);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
