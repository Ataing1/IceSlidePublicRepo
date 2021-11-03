package com.andrew.game.screens.menu;

import com.andrew.game.enums.NavigationEvent;
import com.andrew.game.slip.Slip;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;

/**
 * todo add buttons to sort collected characters by rarity
 * shows all the characters the user has acquired, and the locked ones they haven't acquired yet.
 */
public class CharacterSelectionScreen extends BaseMenuScreen {

    private final TextureAtlas textureAtlas;
    private Image currentImage;

    public CharacterSelectionScreen(Slip game) {
        super(game);
        textureAtlas = new TextureAtlas("sprites.txt");
    }

    @Override
    public void show() {
        super.show();
        Table spriteOptionsTable = new Table();
        spriteOptionsTable.pad(BaseMenuScreen.DEFAULT_UNIT);
        spriteOptionsTable.setBackground(mySkin.getDrawable("NoTitleWindowRihn"));
        spriteOptionsTable.defaults().space(BaseMenuScreen.DEFAULT_UNIT).top();
        int i = 0;
        for (int spriteID : game.accountManager.getAccountObject().getSprites()) {
            if (i % 4 == 0) {
                spriteOptionsTable.row();
            }
            spriteOptionsTable.add(new SpriteButton(spriteID)).size(DEFAULT_UNIT * 12).grow();
            i++;
        }
        if (i < 4) {
            for (; i < 4; i++) {
                spriteOptionsTable.add().size(DEFAULT_UNIT * 12).grow();
            }
        }
        ScrollPane scrollpane = new ScrollPane(spriteOptionsTable);
        scrollpane.setScrollingDisabled(true, false);
        Table container = new Table();
        container.add(scrollpane).grow();

        Table currentlySelectedSprite = new Table();
        Label selectedSpriteLabel = new Label("Current Player", new Label.LabelStyle(assets.buttonLightFont, Color.DARK_GRAY));
        selectedSpriteLabel.setAlignment(Align.center);
        currentImage = new Image();
        setCurrentSprite();
        currentlySelectedSprite.add(selectedSpriteLabel).pad(DEFAULT_UNIT).row();
        currentlySelectedSprite.add(currentImage).size(DEFAULT_UNIT * 12).pad(DEFAULT_UNIT);
        currentlySelectedSprite.setBackground(mySkin.getDrawable("NoTitleWindow"));

        Table fullTable = new Table();
        fullTable.pad(BaseMenuScreen.DEFAULT_UNIT);
        fullTable.add(container).width(Gdx.graphics.getWidth() * .6f).grow();
        fullTable.add(currentlySelectedSprite).expand();
        topParent.add(fullTable).colspan(TOP_PARENT_COLUMN).grow();

    }

    private void setCurrentSprite() {
        int i = game.accountManager.getAccountObject().getSelectedSpriteID();
        System.out.println(i);
        currentImage.setDrawable(new SpriteDrawable(textureAtlas.createSprite(i + "-down")));

    }


    private class SpriteButton extends Button {

        public SpriteButton(final int spriteID) {
            //setDebug(true, true);
            ButtonStyle buttonStyle = new ButtonStyle();
            buttonStyle.up = new SpriteDrawable(textureAtlas.createSprite(spriteID + "-down"));
            buttonStyle.down = new SpriteDrawable(textureAtlas.createSprite(spriteID + "-down"));
            setStyle(buttonStyle);
            //setSize(BUTTON_SIZE, BUTTON_SIZE);
            addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    System.out.println("clicked");
                    buttonClickedSound();
                    game.accountManager.getAccountObject().setSelectedSpriteID(spriteID);
                    game.accountManager.updateAccountObject("Currently Selected Sprite");
                    setCurrentSprite();
                    return true;
                }
            });
        }


    }


    @Override
    protected void switchScreen(NavigationEvent navigationEvent) {
        if (navigationEvent == NavigationEvent.CHARACTER_SELECT) return;
        super.switchScreen(navigationEvent);
    }

    @Override
    public void dispose() {
        super.dispose();
        textureAtlas.dispose();
    }
}
