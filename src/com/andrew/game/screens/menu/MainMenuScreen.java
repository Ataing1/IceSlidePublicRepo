package com.andrew.game.screens.menu;


import com.andrew.game.UI.EnterUsernameDialog;
import com.andrew.game.UI.GameMode;
import com.andrew.game.UI.OutOfLives;
import com.andrew.game.enums.NavigationEvent;
import com.andrew.game.interfaces.MyCallbackEmpty;
import com.andrew.game.screens.gameplay.CasualScreen;
import com.andrew.game.screens.gameplay.ChallengeScreen;
import com.andrew.game.slip.Prefs;
import com.andrew.game.slip.Slip;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;


//todo add a table above the game play, that checks if the user is anonymous or not
// if the user is not anonymous display a label on the table saying "welcome User\n ___Username___"
//  if anonymous, label "Anonymous User. Create an account to save progress." then put a button underneath it taking you to account screen.
public class MainMenuScreen extends BaseMenuScreen {

    public static final String CASUAL = "CASUAL";
    public static final String CHALLENGE = "CHALLENGE";
    public static final String HARDCORE = "HARDCORE";

    private Label greetingLabel;

    public MainMenuScreen(Slip game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        greetingLabel = new Label("", new Label.LabelStyle(assets.settingsFont, Color.WHITE));
        greetingLabel.setAlignment(Align.center);
        getGreeting();


        Table gameTable = new Table();
        gameTable.pad(BaseMenuScreen.DEFAULT_UNIT);
       /* gameTable.add(getHistoryTable(mySkin)).growY().uniform();
        gameTable.add().grow();
        gameTable.add(getGameModeTable(mySkin)).fillX().uniform();

        */
        gameTable.add(getGameModeTable(mySkin));
       /* gameTable.setSize(Gdx.graphics.getWidth(), gameTable.getPrefHeight());
        gameTable.setPosition(0, 0);
        gameTable.toBack();
        stage.addActor(gameTable);*/
        //gameTable.setDebug(true,true);

        Table lifeTrophyTable = new Table();
        lifeTrophyTable.add(game.accountManager.getLivesObject().getLivesTable(mySkin, assets)).row();
        lifeTrophyTable.add(game.accountManager.getTrophyObject().getTrophyTable(mySkin, assets));

        topParent.row();
        topParent.add(lifeTrophyTable).padLeft(DEFAULT_UNIT).uniform();
        topParent.add(getGreetingTable()).growX();
        topParent.add().uniform().row();
        topParent.add(gameTable).colspan(TOP_PARENT_COLUMN).growY();

       /* topParent.add(game.accountManager.getLives().getLivesTable(mySkin, assets)).padLeft(BaseMenuScreen.DEFAULT_PADDING).uniform();
        topParent.add(getGreetingTable()).growX();
        topParent.add().uniform();
        topParent.row();
        topParent.add(game.accountManager.getTrophy().getTrophyTable(mySkin, assets)).padLeft(BaseMenuScreen.DEFAULT_PADDING).uniform();
        topParent.add().growX();*/

        //todo if user is anonymous. track number of times he has entered the main menu, and after a certain number, ask the user again whether he wants to sign up
        //if no reset counter
    }


    private void getGreeting() {
        if (!game.fireBaseServices.isAnonymous()) {
            //Timer timer = new Timer();
            if (game.accountManager.getAccountObject().getUsername() == null) {//nothing was retrieved from getUserInfo() but the user is not anonymous, so ask for username to create database
                new EnterUsernameDialog(mySkin, stage, game, new MyCallbackEmpty() {
                    @Override
                    public void onCallback() {
                        greetingLabel.setText("Welcome " + game.accountManager.getAccountObject().getUsername());
                    }
                }).show(stage);
            } else {
                greetingLabel.setText("Welcome " + game.accountManager.getAccountObject().getUsername());
                //todo potentially re add timer to make it look like the game is actually loading a lot of info
               /* if (game.accountManager.getUserInfo().containsKey("Username")) {
                    timer.stop();

                } else {
                    greetingLabel.setText("Loading...");
                    Timer.Task task = new Timer.Task() {
                        @Override
                        public void run() {
                            getGreeting();
                        }
                    };
                    timer.scheduleTask(task, 0f, 1f);
                }*/
            }

        }
    }


    public Table getGameModeTable(final Skin mySkin) {


        Label gameModeLabel = new Label("GAME MODE: ", new Label.LabelStyle(assets.textFont, Color.DARK_GRAY));
        gameModeLabel.setAlignment(Align.center);

        final Label gameType = new Label(game.getPreferences().getString(Prefs.GAME_MODE.label, CHALLENGE), new Label.LabelStyle(assets.textFont, Color.DARK_GRAY));
        gameType.setAlignment(Align.center);

        TextButton changeGameMode = new TextButton("Change Mode", mySkin);
        changeGameMode.getLabel().setStyle(new Label.LabelStyle(assets.textFont, Color.WHITE));
        changeGameMode.pad(DEFAULT_UNIT);

        TextButton readyButton = new TextButton("PLAY", mySkin, "yellow");
        readyButton.getLabel().setStyle(new Label.LabelStyle(assets.settingsFont, Color.DARK_GRAY));

        Table verticalGroup = new Table();
        verticalGroup.pad(DEFAULT_UNIT);
        verticalGroup.defaults().space(DEFAULT_UNIT);
        verticalGroup.add(gameModeLabel);
        verticalGroup.add(gameType).row();
        verticalGroup.add(changeGameMode).colspan(2).grow();

        Table vertTable = new Table();
        vertTable.setBackground(mySkin.getDrawable("NoTitleWindow"));
        vertTable.add(verticalGroup).grow();

        Table table = new Table();
        table.defaults().space(BaseMenuScreen.DEFAULT_UNIT);
        table.add(vertTable).grow().uniform().row();
        table.add(readyButton).grow();

        changeGameMode.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buttonClickedSound();
                new GameMode(mySkin, game, new MyCallbackEmpty() {
                    @Override
                    public void onCallback() {
                        gameType.setText(game.getPreferences().getString(Prefs.GAME_MODE.label, CHALLENGE));
                    }
                }).show(stage);
            }
        });

        readyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buttonClickedSound();
                switch (game.getPreferences().getString(Prefs.GAME_MODE.label, CHALLENGE)) {
                    case CASUAL:
                        System.out.println("playing casual");
                        game.setScreen(new CasualScreen(game, game.accountManager.getAccountObject().getTrophy()));
                        dispose();
                        break;
                    case CHALLENGE:
                        System.out.println("playing challenge");
                        if (game.accountManager.getLivesObject().getLives() <= 0) {
                            new OutOfLives(mySkin, game, new MyCallbackEmpty() {
                                @Override
                                public void onCallback() {
                                    if (game.accountManager.getLivesObject().getLives() > 0) {
                                        game.setScreen(new ChallengeScreen(game, game.accountManager.getAccountObject().getTrophy()));
                                        dispose();
                                    }
                                }
                            }).show(stage);
                        } else {
                            game.setScreen(new ChallengeScreen(game, game.accountManager.getAccountObject().getTrophy()));
                            dispose();
                        }
                        break;

                    case HARDCORE:
                        System.out.println("playing hardcore");
                        break;
                    //hardcore
                }

            }
        });

        table.layout();
        return table;
    }

    private Table getGreetingTable() {
        if (game.fireBaseServices.isAnonymous()) return null;
        Table table = new Table();
        table.setBackground(mySkin.getDrawable("NoTitlePurpleBlue64"));
        table.add(greetingLabel).padLeft(BaseMenuScreen.DEFAULT_UNIT).padRight(BaseMenuScreen.DEFAULT_UNIT);

        Table growTable = new Table();
        growTable.add(table);
        return growTable;
    }

    /*todo use this code, and other Gdx.net stuff to implement a better shop, and maybe live gameplay
       /* buttonRandom.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
                Net.HttpRequest httpRequest = requestBuilder.newRequest().method(Net.HttpMethods.GET).url("https://andrew.andrew-worker.workers.dev").build();
                Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
                    @Override
                    public void handleHttpResponse(Net.HttpResponse httpResponse) {
                        System.out.println(httpResponse.getResultAsString());
                    }

                    @Override
                    public void failed(Throwable t) {

                    }

                    @Override
                    public void cancelled() {

                    }
                });
                return true;
            }

        });

        */
    @Override
    protected void switchScreen(NavigationEvent navigationEvent) {
        if (navigationEvent == NavigationEvent.MAIN_MENU) return;
        super.switchScreen(navigationEvent);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}