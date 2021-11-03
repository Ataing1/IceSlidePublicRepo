package com.andrew.game.screens.gameplay;

import com.andrew.game.MazeJson;
import com.andrew.game.UI.SignInUI;
import com.andrew.game.enums.CongratulationCommand;
import com.andrew.game.interfaces.MyCallbackBoolean;
import com.andrew.game.interfaces.MyCallbackCongratulation;
import com.andrew.game.interfaces.MyCallbackEmpty;
import com.andrew.game.screens.gameplay.congratulation.TutorialCongratulation;
import com.andrew.game.screens.menu.BaseMenuScreen;
import com.andrew.game.screens.menu.MainMenuScreen;
import com.andrew.game.slip.Slip;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;

public class TutorialScreen extends BaseGame {

    private int tutorialStatus;
    private Table wordyTable;
    private Label label;
    private boolean statusFailed = false;


    public TutorialScreen(Slip game, int trophyCount) {
        super(game, trophyCount);
        tutorialStatus = 0;

    }

    public TutorialScreen(Slip game, int trophyCount, boolean statusFailed) {
        super(game, trophyCount);
        tutorialStatus = 0;
        this.statusFailed = statusFailed;
    }

    @Override
    public MazeJson getMazeJson(int indexLevel) {
        System.out.println("using tutorial mazeJson()"); //hardcoded because we want to keep the tutorial maze the same
        return new Json().fromJson(MazeJson.class, Gdx.files.internal("tutorial/level" + 1 + ".json"));
    }

    @Override
    public void show() {
        super.show();
        wordyTable = new Table();
        wordyTable.setBackground(mySkin.getDrawable("NoTitleWindow"));
        label = new Label("Thanks for helping me get out of this Maze!\nSwipe to move me.", new Label.LabelStyle(assets.buttonLightFont, Color.BLACK));
        label.setAlignment(Align.center);

        label.setWrap(true);

        Table buttonTable = new Table();
        buttonTable.defaults().pad(defaultPadding);
        TextButton signIn = new TextButton("Sign In", mySkin);
        TextButton skipTutorial = new TextButton("Skip Tutorial", mySkin);

        buttonTable.add(skipTutorial).growX();
        buttonTable.add(signIn).growX();


        wordyTable.add(label).top().grow().pad(defaultPadding).row();
        wordyTable.add(buttonTable).pad(defaultPadding).growX();

        signIn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new SignInUI(mySkin, game).show(stage);
            }
        });
        skipTutorial.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                exitAnimationFinishedEvent();
            }
        });


        stage.addActor(wordyTable);
        congratulationState = new TutorialCongratulation(game, mySkin, new MyCallbackCongratulation() {
            @Override
            public void onCallback(CongratulationCommand congratulationCommand) {

                Dialog dialog = new Dialog("", mySkin);
                Label label = new Label("Keep your progress safe. Get a magic link sent to your email that'll sign you in instantly", new Label.LabelStyle(assets.buttonLightFont, Color.WHITE));
                label.setAlignment(Align.center);
                label.setWrap(true);
                TextButton continueButton = new TextButton("Continue", mySkin, "gold");
                continueButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        new SignInUI(mySkin, game).show(stage);
                    }
                });
                TextButton later = new TextButton("Later", mySkin);
                later.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        game.fireBaseServices.toast("loading");
                        game.fireBaseServices.signInAnonymously(new MyCallbackBoolean() {
                            @Override
                            public void onCallback(boolean status) {
                                if (status) {
                                    game.loadAccountObject(new MyCallbackEmpty() {
                                        @Override
                                        public void onCallback() {
                                            game.setScreen(new MainMenuScreen(game));
                                            dispose();
                                        }
                                    });
                                } else {
                                    game.fireBaseServices.toast("Oops, it seems like there is a bug in our offline system :(");
                                }
                            }
                        });
                    }
                });
                dialog.getContentTable().pad(BaseMenuScreen.DEFAULT_UNIT);
                dialog.getContentTable().defaults().space(BaseMenuScreen.DEFAULT_UNIT);
                dialog.getContentTable().add(label).width(Gdx.graphics.getWidth() * .5f).colspan(2).row();
                dialog.getContentTable().add(later).growX();
                dialog.getContentTable().add(continueButton).growX();
                dialog.show(stage);
            }
        });
        tutorialStatus = 1;
        //todo if status failed, let the user know and ask if he would like to try logging in again
        if (statusFailed) { //called only from a failed email link, so its safe to assume user is trying to log in or do account stuff
            Label.LabelStyle labelStyle = new Label.LabelStyle(assets.buttonLightFont, Color.DARK_GRAY);
            TextButton textButton = new TextButton("Log In", mySkin, "gold");
            TextButton noThanks = new TextButton("No Thanks", mySkin);
            textButton.getLabel().setStyle(labelStyle);
            noThanks.getLabel().setStyle(labelStyle);
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    new SignInUI(mySkin, game).show(stage);
                }
            });

            Dialog dialog = new Dialog("", mySkin);
            dialog.setKeepWithinStage(true);
            Label message = new Label("The email link may be expired or invalid, would you like to try logging in again?", new Label.LabelStyle(assets.textFont, Color.DARK_GRAY));
            dialog.getContentTable()
                    .add(message)
                    .pad(defaultPadding);
            dialog.getButtonTable().defaults().pad(defaultPadding).growX();
            dialog.button(noThanks);
            dialog.button(textButton);
            // dialog.getButtonTable().add(noThanks, textButton);
            dialog.show(stage);

        }

    }

    @Override
    protected void renderAction(float delta) {
        switch (renderState) {
            case PLAYING:
                gameObject.update(delta);
                break;
            case ANIMATING_EXIT:
                gameObject.animateExit(delta);
                break;
            case WAITING:
                if (gameScreenSound.isPlaying()) {
                    gameScreenSound.stop();
                }
                break;
            case EXITING_GAME:
                fadeSprites(delta);
                break;
            case TUTORIAL: // used while waiting for user interaction
                break;
        }
    }

    @Override
    protected void playerStoppedEvent() {
        if (tutorialStatus == 1) {
            tutorialStatus = 2;
            label.setText("The ice is slippery, so I can't stop until I hit something");
        }
        super.playerStoppedEvent();
    }

    @Override
    public void resize(int width, int height) {
        int blockSideLength = getBlockSideLength(sideBarWidth, NUMBER_OF_BLOCKS_WIDE, NUMBER_OF_BLOCKS_HIGH);

        gameWidth = NUMBER_OF_BLOCKS_WIDE * blockSideLength;
        gameHeight = NUMBER_OF_BLOCKS_HIGH * blockSideLength;
        stageY = (Gdx.graphics.getHeight() - gameHeight) / 2;
        stageX = (int) (Gdx.graphics.getWidth() - gameWidth - borderThickness - stageY);
        gameStage.getViewport().setScreenSize(gameWidth, gameHeight);
        gameStage.getViewport().setScreenPosition(stageX, stageY); //stageY instead of 0
        wordyTable.setSize(Gdx.graphics.getWidth() - gameWidth - 3 * stageY, gameHeight + 2 * borderThickness);
        wordyTable.setPosition(stageY - borderThickness, stageY - borderThickness);

    }

    @Override
    protected void hitExitEvent() {
        stage.clear();
        super.hitExitEvent();
    }

    @Override
    protected void exitAnimationFinishedEvent() {
        congratulationState.show(stage);

        super.exitAnimationFinishedEvent();

    }


}
