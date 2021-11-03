package com.andrew.game.slip;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;


public class Assets {

    public final AssetManager manager;


    public static final String CUSTOM_SKIN = "skin/exportedJson.json";
    public static final String CUSTOM_SKIN_ATLAS = "skin/exportedJson.atlas";


    public static final String COIN_IMAGE = "crystal1";
    public static final String CELEBRATION_SOUND = "sounds/celebration soundeffect Edit 1 Export 1.mp3";
    public static final String MAIN_MENU_SOUND = "sounds/Dream_of_the_forest_(jazzy_mix).mp3";
    public static final String GAME_SCREEN_SOUND = "sounds/Two_Pianos.mp3";
    public static final String IMPACT_SOUND = "sounds/impactSound.mp3";
    public static final String CLICK_SOUND = "sounds/zapsplat_multimedia_button_click_007_53868.mp3";
    public static final String CREDITS = "credit for artists.txt";

    public BitmapFont titleFont;
    public BitmapFont buttonLightFont;
    public BitmapFont settingsFont;
    public BitmapFont textFont;


    public Assets() {
        manager = new AssetManager();
        loadFonts();
    }


    private void loadFonts() {

        int screenWidth = Gdx.graphics.getWidth();

        int fontSizeTitle = (int) Math.ceil(screenWidth * .075f);
        int fontSizeButton = (int) Math.ceil(screenWidth * .035f);
        int fontSizeSettings = (int) Math.ceil(screenWidth * .055f);
        int fontSizeText = (int) Math.ceil(screenWidth * .03f);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/busbold.ttf"));
        FreeTypeFontGenerator.setMaxTextureSize(2048);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        //default colorless fonts
        parameter.size = fontSizeTitle;
        titleFont = generator.generateFont(parameter);
        parameter.size = fontSizeSettings;
        settingsFont = generator.generateFont(parameter);
        parameter.size = fontSizeButton;
        parameter.borderWidth = 1;
        parameter.borderColor = Color.DARK_GRAY;
        buttonLightFont = generator.generateFont(parameter);

        parameter.size = fontSizeText;
        textFont = generator.generateFont(parameter);
        generator.dispose();

    }

    public void loadSkin() {

        ObjectMap<String, Object> Map = new ObjectMap<>();
        Map.put("buttonFont", buttonLightFont);
        Map.put("titleFont", settingsFont);
        Map.put("textFont", textFont);

        SkinLoader.SkinParameter skinParameter = new SkinLoader.SkinParameter(CUSTOM_SKIN_ATLAS, Map);
        manager.load(CUSTOM_SKIN, Skin.class, skinParameter);

    }

    public Skin getSkin() {
        return manager.get(CUSTOM_SKIN);
    }

    public void unloadSkin() {
        manager.unload(CUSTOM_SKIN);

    }


    public void dispose() {
        //work around to avoid the Pix map fatal exception error
        System.out.println("removing 1");
        getSkin().remove("buttonFont", BitmapFont.class);
        System.out.println("removing 3");
        getSkin().remove("settingsFont", BitmapFont.class);
        System.out.println("removing 4");
        getSkin().remove("titleFont", BitmapFont.class);
        System.out.println("manager dispose before");
        manager.dispose();
        System.out.println("manager dispose after");


    }


}
