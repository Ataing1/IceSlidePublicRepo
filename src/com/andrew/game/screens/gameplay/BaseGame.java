package com.andrew.game.screens.gameplay;

import com.andrew.game.MazeJson;
import com.andrew.game.enums.Event;
import com.andrew.game.enums.MenuEvent;
import com.andrew.game.observer.Observer;
import com.andrew.game.observer.ObserverMenu;
import com.andrew.game.player.GameObject;
import com.andrew.game.screens.gameplay.congratulation.ChallengeCongratulation;
import com.andrew.game.screens.menu.BaseMenuScreen;
import com.andrew.game.screens.menu.MainMenuScreen;
import com.andrew.game.slip.Assets;
import com.andrew.game.slip.Menu;
import com.andrew.game.slip.Slip;
import com.andrew.game.statemachines.CongratulationState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class BaseGame extends GestureDetector.GestureAdapter implements Screen, ObserverMenu, Observer {

    //Each GameScreen Has-A:
    protected final Slip game;
    protected final GestureDetector gestureDetector;
    protected final Assets assets;
    protected final Stage stage;
    protected final Stage gameStage;
    protected final Menu menu;
    protected final int trophyCount;
    protected final int minSteps;
    protected final long startTime;


    //game layout variables that manage how the gameStage looks
    protected int gameWidth;
    protected int gameHeight;
    protected int stageX, stageY;
    protected final int NUMBER_OF_BLOCKS_WIDE, NUMBER_OF_BLOCKS_HIGH;
    protected final int sideBarWidth = (int) (Gdx.graphics.getWidth() * .115f);
    protected float borderThickness = 6.095238095f * Gdx.graphics.getDensity();
    protected float defaultPadding = BaseMenuScreen.DEFAULT_UNIT;

    //finite state machines
    protected final GameObject gameObject; //handles game-play and drawing
    public CongratulationState congratulationState; //handles congratulation page and post game stuff

    protected final SpriteBatch spriteBatch;
    protected final TextureAtlas textureAtlas;
    protected final Sprite borderVertical;
    protected final Sprite borderHorizontal;

    protected Music gameScreenSound;
    protected Sound impactSound;
    protected Sound celebrationSound;


    //UI
    protected Skin mySkin;
    protected Window settingsWindow;
    protected Table accountTable;
    protected ImageButton settings;

    ShapeRenderer shapeRenderer;

    ArrayList<Color> colorCorner = new ArrayList<>(4);
    ArrayList<Color> colorGoal = new ArrayList<>(4);

    protected enum RenderStates {PLAYING, ANIMATING_EXIT, EXITING_GAME, WAITING, TUTORIAL}

    protected RenderStates renderState = RenderStates.PLAYING;

    private Color getRandomColor() {
        //replace threadlocal with getRandom(min,max) to target proper colors rgb
        return new Color(ThreadLocalRandom.current().nextFloat(), ThreadLocalRandom.current().nextFloat(), ThreadLocalRandom.current().nextFloat(), ThreadLocalRandom.current().nextFloat());
    }

    private static float getRandomFloat(float min, float max) {
        return min + ThreadLocalRandom.current().nextFloat() * (max - min);
    }


    public BaseGame(Slip game, int trophyCount) {
        //Init
        startTime = TimeUtils.millis();
        this.game = game;
        this.trophyCount = trophyCount;
        stage = new Stage(new ScreenViewport());

        assets = game.getAssets();
        menu = new Menu(game.getPreferences());
        gestureDetector = new GestureDetector(this);

        //get Data out of mazeJson
        MazeJson mazeJson = getMazeJson(trophyCount);
        minSteps = mazeJson.minSteps;
        NUMBER_OF_BLOCKS_WIDE = Integer.parseInt(mazeJson.mazeSize.x);
        NUMBER_OF_BLOCKS_HIGH = Integer.parseInt(mazeJson.mazeSize.y);
        int blockSideLength = getBlockSideLength(sideBarWidth, NUMBER_OF_BLOCKS_WIDE, NUMBER_OF_BLOCKS_HIGH);

        //setup gameStage
        gameWidth = NUMBER_OF_BLOCKS_WIDE * blockSideLength;
        gameHeight = NUMBER_OF_BLOCKS_HIGH * blockSideLength;
        stageX = ((Gdx.graphics.getWidth() - sideBarWidth) - gameWidth) / 2 + sideBarWidth;
        stageY = (Gdx.graphics.getHeight() - gameHeight) / 2;
        gameStage = new Stage(new FitViewport(gameWidth, gameHeight));
        gameStage.getViewport().setScreenSize(gameWidth, gameHeight);
        gameStage.getViewport().setScreenPosition(stageX, stageY); //stageY instead of 0

        /*
        responsibility of code at runtime to determine how actors react to touch.
        For example, when the player runs out of steps, the gameObject should ignore any gestures detected
         */
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, gestureDetector));

        spriteBatch = new SpriteBatch();
        textureAtlas = new TextureAtlas("sprites.txt");
        borderVertical = textureAtlas.createSprite("Vertical frame");
        borderHorizontal = textureAtlas.createSprite("horizontal frame");
        int spriteID;
        if (game.fireBaseServices.hasUser()) {
            spriteID = game.accountManager.getAccountObject().getSelectedSpriteID();
        } else {
            spriteID = 1;
        }
        gameObject = new GameObject(textureAtlas, spriteID, gameWidth, gameHeight, blockSideLength, mazeJson, NUMBER_OF_BLOCKS_HIGH, NUMBER_OF_BLOCKS_WIDE);
        gameObject.addObserver(this);
        menu.addObserver(this);

        for (int i = 0; i < 4; i++) {
            colorCorner.add(getRandomColor());
            colorGoal.add(getRandomColor());
        }

    }

    @Override
    public void onNotify(MenuEvent event) {
        switch (event) {
            case MUSIC_ON:
                gameScreenSound.play();
                break;
            case MUSIC_OFF:
                gameScreenSound.pause();
                break;
            case GO_HOME:
                goHome();
        }
    }

    public MazeJson getMazeJson(int indexLevel) {
        System.out.println("using default mazeJson()");
        return new Json().fromJson(MazeJson.class, Gdx.files.internal("mazes/level" + indexLevel + ".json"));
    }

    protected int getBlockSideLength(int sideBarWidth, int NUMBER_OF_BLOCKS_WIDE, int NUMBER_OF_BLOCKS_HIGH) {
        if ((Gdx.graphics.getWidth() - sideBarWidth) / (double) Gdx.graphics.getHeight() < ((double) NUMBER_OF_BLOCKS_WIDE / NUMBER_OF_BLOCKS_HIGH)) {
            System.out.println("extra room on top and bottom");
            return (int) Math.min((float) (Gdx.graphics.getWidth() - sideBarWidth) / NUMBER_OF_BLOCKS_WIDE, 48.30188679f * Gdx.graphics.getDensity());
        } else {
            System.out.println("extra room left and right");
            return (int) Math.min((float) Gdx.graphics.getHeight() / NUMBER_OF_BLOCKS_HIGH, 48.30188679f * Gdx.graphics.getDensity());
        }
    }

    @Override
    public void show() {
        /*todo add confetti
         *
         * */
        System.out.println("base show called");
        assets.loadSkin();
        assets.manager.load(Assets.CELEBRATION_SOUND, Sound.class);
        assets.manager.load(Assets.GAME_SCREEN_SOUND, Music.class);
        assets.manager.load(Assets.IMPACT_SOUND, Sound.class);
        assets.manager.finishLoading();
        mySkin = assets.getSkin();
        gameScreenSound = assets.manager.get(Assets.GAME_SCREEN_SOUND);
        impactSound = assets.manager.get(Assets.IMPACT_SOUND);
        celebrationSound = assets.manager.get(Assets.CELEBRATION_SOUND);
        gameScreenSound.setLooping(true);
        if (menu.playMusic) {
            gameScreenSound.play();
        } else {
            gameScreenSound.pause();
        }

        settingsWindow = menu.getWindowGame(mySkin);
        if (game.fireBaseServices.hasUser()) {
            loadAds();
            accountTable = game.accountManager.getTable(mySkin, assets);
            accountTable.setVisible(false);//set true when congratulation window shows
            stage.addActor(accountTable);
        }
        settings = new ImageButton(mySkin);
        settings.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                settingsWindow.setVisible(true);
                return true;
            }
        });


        stage.addActor(settingsWindow);
        System.out.println("base show end");

        shapeRenderer = new ShapeRenderer();
    }

    public void loadAds() {
        if (!game.accountManager.getAccountObject().getAdFree()) {
            game.fireBaseServices.loadInterstitialAd();
            game.fireBaseServices.loadSkipLevelAd();
        }
    }

    protected void renderAction(float delta) {
        switch (renderState) {
            case PLAYING:
                gameObject.update(delta);
                break;
            case ANIMATING_EXIT:
                gameObject.animateExit(delta);
                break;
            case WAITING:
                game.accountManager.update(delta);
                if (gameScreenSound.isPlaying()) {
                    gameScreenSound.stop();
                }
                break;
            case EXITING_GAME:
                fadeSprites(delta);
                break;
        }
    }

    @Override
    public void render(float delta) {

        renderAction(delta);
        Gdx.gl.glClearColor(0.370588235f, 0.592156863f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getViewport().apply();
        shapeRenderer.setProjectionMatrix(stage.getViewport().getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                //todo get random color is too recource intensive/wasteful. but it has the right idea with lerping. maybe every 10 seconds or so, change the target color.
                //todo also, change the range so teh color can only be certain shades of blue
                colorCorner.get(0).lerp(colorGoal.get(0), delta),
                colorCorner.get(1).lerp(colorGoal.get(1), delta),
                colorCorner.get(2).lerp(colorGoal.get(2), delta),
                colorCorner.get(3).lerp(colorGoal.get(3), delta));
        //shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Color.ROYAL, Color.ROYAL, Color.SKY, Color.SKY);
        for (int i = 0; i < 4; i++) {
            if (colorCorner.get(i).equals(colorGoal.get(i))) {
                colorGoal.set(i, getRandomColor());
            }
        }
        shapeRenderer.end();
        gameStage.getViewport().apply();
        spriteBatch.setProjectionMatrix(gameStage.getViewport().getCamera().combined);
        gameObject.draw(spriteBatch);//gameObject starts and ends the sprite batch

        stage.getViewport().apply();
        spriteBatch.setProjectionMatrix(stage.getViewport().getCamera().combined);
        spriteBatch.begin();
        drawBorder();
        spriteBatch.end();

        stage.act();
        stage.draw();

    }

    protected void fadeSprites(float delta) {
        //todo check if gamestage.addActions(Actions.fadeout) works
        spriteBatch.setColor(255, 255, 255, spriteBatch.getColor().a - delta);
    }

    protected void drawBorder() {


        spriteBatch.draw(borderHorizontal, stageX - borderThickness, stageY - borderThickness, gameWidth + borderThickness * 2, borderThickness);
        spriteBatch.draw(borderHorizontal, stageX - borderThickness, stageY + gameHeight, gameWidth + borderThickness * 2, borderThickness);
        spriteBatch.draw(borderVertical, stageX - borderThickness, stageY, borderThickness, gameHeight);
        spriteBatch.draw(borderVertical, stageX + gameWidth, stageY, borderThickness, gameHeight);
    }


    @Override
    public void resize(int width, int height) {
        System.out.println("resize called");
        stage.getViewport().update(width, height, true);
        //resize settings window to reflect new width and height. ALL OF THESE MUST BE DONE
        settingsWindow.validate();
        settingsWindow.padTop(BaseMenuScreen.DEFAULT_UNIT * 6.5f);
        settingsWindow.setSize(Gdx.graphics.getWidth() / 1.3f, Gdx.graphics.getHeight() / 1.3f);
        settingsWindow.setPosition(Gdx.graphics.getWidth() / 2f - settingsWindow.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - settingsWindow.getHeight() / 2f);
        settingsWindow.toFront();

        int blockSideLength = getBlockSideLength(sideBarWidth, NUMBER_OF_BLOCKS_WIDE, NUMBER_OF_BLOCKS_HIGH);
        gameWidth = NUMBER_OF_BLOCKS_WIDE * blockSideLength;
        gameHeight = NUMBER_OF_BLOCKS_HIGH * blockSideLength;
        stageX = ((Gdx.graphics.getWidth() - sideBarWidth) - gameWidth) / 2 + sideBarWidth;
        stageY = (Gdx.graphics.getHeight() - gameHeight) / 2;
        gameStage.getViewport().setScreenSize(gameWidth, gameHeight);
        gameStage.getViewport().setScreenPosition(stageX, stageY); //stageY instead of 0
        System.out.println("resize end");
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return gameObject.fling(velocityX, velocityY, button);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        menu.removeObserver(this);
        gameObject.removeObserver(this);
        spriteBatch.dispose();
        textureAtlas.dispose();
        stage.dispose();
        assets.unloadSkin();
        assets.manager.unload(Assets.GAME_SCREEN_SOUND);
        assets.manager.unload(Assets.IMPACT_SOUND);
        shapeRenderer.dispose();
    }

    public void goHome() {
        game.fireBaseAnalytics.logLevelEvent(trophyCount, false);
        renderState = RenderStates.EXITING_GAME;
        System.out.println("going home");
        SequenceAction sequenceAction = new SequenceAction();
        sequenceAction.addAction(Actions.fadeOut(1f));
        sequenceAction.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        }));
        stage.addAction(sequenceAction);
    }

    @Override
    public void onNotify(Event event) {
        switch (event) {
            case IMPACT:
                impactEvent();
                break;
            case PLAYER_STOPPED:
                playerStoppedEvent();
                break;
            case LEGAL_MOVE:
                legalMoveEvent();
                break;
            case EXIT_ANIM_FINISHED:
                exitAnimationFinishedEvent();
                break;
            case HIT_EXIT:
                hitExitEvent();
                break;

        }
    }

    protected void impactEvent() {
        if (menu.playFX) {
            impactSound.play(.2f);
        }
    }

    protected void playerStoppedEvent() {
    }

    protected void legalMoveEvent() {
    }

    protected void exitAnimationFinishedEvent() {
        renderState = RenderStates.WAITING;
        if (menu.playFX) {
            celebrationSound.play(.2f);
        }
    }

    protected void hitExitEvent() {
        game.fireBaseAnalytics.logLevelEvent(trophyCount, true);
        if (congratulationState instanceof ChallengeCongratulation) {
            congratulationState.update(game.accountManager.registerWin(), minSteps, gameObject.getSteps(), startTime);
        } else {
            //you don't earn trophy in casual mode
            congratulationState.update(0, minSteps, gameObject.getSteps(), startTime);
        }
        renderState = RenderStates.ANIMATING_EXIT;
    }

}


