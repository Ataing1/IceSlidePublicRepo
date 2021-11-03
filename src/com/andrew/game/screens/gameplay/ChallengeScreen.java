package com.andrew.game.screens.gameplay;

import com.andrew.game.MazeJson;
import com.andrew.game.UI.FailureWindow;
import com.andrew.game.UI.OutOfLives;
import com.andrew.game.UI.SkipLevelDialog;
import com.andrew.game.enums.CongratulationCommand;
import com.andrew.game.interfaces.MyCallbackBoolean;
import com.andrew.game.interfaces.MyCallbackCongratulation;
import com.andrew.game.interfaces.MyCallbackEmpty;
import com.andrew.game.observer.Observer;
import com.andrew.game.screens.gameplay.congratulation.ChallengeCongratulation;
import com.andrew.game.slip.Prefs;
import com.andrew.game.slip.Slip;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class ChallengeScreen extends BaseGame implements Observer {

    final int maxSteps;
    Table sideBarTable;
    Label stepsRemaining;

    public ChallengeScreen(Slip game, int trophyCount) {
        super(game, trophyCount);
        maxSteps = (int) Math.floor(minSteps * 1.2);
        game.accountManager.getLivesObject().spendLife();
    }

    //todo mazejson can get values that are out of bounds
    @Override
    public MazeJson getMazeJson(int indexLevel) {
        int i;
        if (game.accountManager.getAccountObject().getCurrentMap() == 0) {
            i = ThreadLocalRandom.current().nextInt(Math.max(indexLevel - 10, 0), indexLevel + 10);
            game.accountManager.getAccountObject().setCurrentMap(i);
            game.accountManager.updateAccountObject("Map");
        } else {
            i = game.accountManager.getAccountObject().getCurrentMap();

        }
        System.out.println("overridden Json Called # " + i);

        return new Json().fromJson(MazeJson.class, Gdx.files.internal("mazes/level" + i + ".json"));
    }

    private void congratulationInit(Slip game) {
        congratulationState = new ChallengeCongratulation(game, mySkin, new MyCallbackCongratulation() {
            @Override
            public void onCallback(CongratulationCommand congratulationCommand) {

                switch (congratulationCommand) {
                    case GO_NEXT:
                        goNextLevel(false);
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
        System.out.println("override show called");
        super.show(); //must be called first becuase it initializes skin
        //load ad only if not adFree
        if (!game.accountManager.getAccountObject().getAdFree()) {
            game.fireBaseServices.loadExtraMovesRewardedAd();
        }

        setSideBarTable();
        congratulationInit(game);
        System.out.println("override show finished");

    }

    @Override
    public void goHome() {
        super.goHome();
    }

    private void setSideBarTable() {
        Label.LabelStyle labelStyle = new Label.LabelStyle(assets.buttonLightFont, Color.WHITE);
        Label lifeLabel = new Label("Lives\n" + game.accountManager.getLivesObject().getLives(), labelStyle);
        //Label levelLabel = new Label("Level\n" + (indexLevel + 1), labelStyle);
        lifeLabel.setAlignment(Align.center);
        //levelLabel.setAlignment(Align.center);
        Button skipLevel = new TextButton("Skip", mySkin);
        skipLevel.align(Align.center);
        //we don't register a loss for skipping the level because we want to encourage the user to watch ads
        skipLevel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new SkipLevelDialog(game, mySkin, new MyCallbackBoolean() {
                    @Override
                    public void onCallback(boolean status) {
                        if (status) {
                            goNextLevel(true);
                        }
                    }
                }).show(stage);
            }
        });

        stepsRemaining = new Label("Moves\n" + (maxSteps - gameObject.getSteps()), labelStyle);
        stepsRemaining.setAlignment(Align.center);

        sideBarTable = new Table();  //overall sideTable
        VerticalGroup infoBar = new VerticalGroup();
        infoBar.align(Align.center);

        infoBar.addActor(lifeLabel);
        //infoBar.addActor(levelLabel);
        infoBar.addActor(stepsRemaining);
        infoBar.addActor(skipLevel);

        // VerticalGroup buttonBar = new VerticalGroup();
        // buttonBar.align(Align.center);
        //buttonBar.addActor(settings);

        sideBarTable.add(infoBar).padTop(Gdx.graphics.getHeight() * .05f).row();
        sideBarTable.add().grow().row();
        sideBarTable.add(settings).padBottom(Gdx.graphics.getHeight() * .05f).growX();

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
    protected void playerStoppedEvent() {
        if (gameObject.getSteps() == maxSteps) {
            renderState = RenderStates.WAITING;
            gameObject.setGameOver(true);
            game.fireBaseAnalytics.logLevelEvent(trophyCount, false);
            new FailureWindow(game.accountManager.registerLoss(), mySkin, game, new MyCallbackCongratulation() {
                @Override
                public void onCallback(CongratulationCommand congratulationCommand) {
                    switch (congratulationCommand) {
                        case EXTRA_MOVES:
                            goExtraMoves();
                            break;
                        case RETRY:
                            retryLevel(false);
                            break;
                        case GO_HOME:
                            goHome();
                            break;
                        default:
                            throw new UnsupportedOperationException(String.format(Locale.US, "%s is not an accepted callback enum", congratulationCommand));
                    }
                }
            }).show(stage);
            showAccountTable();
        }
        super.playerStoppedEvent();
    }

    @Override
    protected void legalMoveEvent() {
        stepsRemaining.setText("Moves\n" + (maxSteps - gameObject.getSteps()));
        super.legalMoveEvent();
    }

    @Override
    protected void hitExitEvent() {
        game.accountManager.getLivesObject().addLives(1);
        game.accountManager.getAccountObject().setCurrentMap(0);
        game.accountManager.updateAccountObject("Map");
        super.hitExitEvent();
    }

    @Override
    protected void exitAnimationFinishedEvent() {
        congratulationState.show(stage);
        showAccountTable();
        super.exitAnimationFinishedEvent();
    }


    private void showAccountTable() {
        sideBarTable.setVisible(false);
        accountTable.setVisible(true);
        accountTable.toFront();
    }

    private void showGameBar() {
        sideBarTable.setVisible(true);
        accountTable.setVisible(false);
        accountTable.toBack();
    }



    private boolean lifeCheck(final MyCallbackEmpty myCallbackEmpty) {
        if (game.accountManager.getLivesObject().getLives() <= 0) {
            new OutOfLives(mySkin, game, new MyCallbackEmpty() {
                @Override
                public void onCallback() {
                    myCallbackEmpty.onCallback();
                }
            }).show(stage);
            return false;
        } else {
            return true;
        }
    }

    private void retryLevel(boolean fromCallback) {
        if (lifeCheck(new MyCallbackEmpty() {
            @Override
            public void onCallback() {
                System.out.println("calledBack");
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("retrying level");
                        retryLevel(true);
                    }
                });
            }
        })) {
            SequenceAction sequenceAction = new SequenceAction();
            sequenceAction.addAction(Actions.fadeOut(1f));
            sequenceAction.addAction(Actions.run(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(new ChallengeScreen(game, trophyCount));
                    dispose();
                }
            }));
            stage.addAction(sequenceAction);
        } else {
            if (fromCallback) {
                goHome();
            }
        }

    }

    private void goNextLevel(boolean skip) {
        renderState = RenderStates.EXITING_GAME;
        game.getPreferences().putInteger(Prefs.CHALLENGE_SEED.label, -1);
        game.getPreferences().flush();
        if (game.accountManager.getLivesObject().getLives() <= 0 && !skip) {
            new OutOfLives(mySkin, game, new MyCallbackEmpty() {
                @Override
                public void onCallback() {
                    if (game.accountManager.getLivesObject().getLives() <= 0) {
                        goHome();
                    } else {
                        goNextLevel(false);
                    }


                }
            }).show(stage);
        } else {
            SequenceAction sequenceAction = new SequenceAction();
            sequenceAction.addAction(Actions.fadeOut(1f));
            sequenceAction.addAction(Actions.run(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(new ChallengeScreen(game, game.accountManager.getAccountObject().getTrophy()));
                    dispose();
                }
            }));
            stage.addAction(sequenceAction);
        }

    }

    private void goExtraMoves() {
        renderState = RenderStates.PLAYING;
        gameObject.setGameOver(false);
        gameObject.addExtraSteps();
        showGameBar();
        stepsRemaining.setText("Moves:\n" + (maxSteps - gameObject.getSteps()));
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
