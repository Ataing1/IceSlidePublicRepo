package com.andrew.game.screens.menu;

import com.andrew.game.UI.DailyReward;
import com.andrew.game.UI.DailyRewardWindow;
import com.andrew.game.UI.TapAnyWhereCloseDialog;
import com.andrew.game.UI.TapCloseDialog;
import com.andrew.game.enums.NavigationEvent;
import com.andrew.game.interfaces.MyCallbackBoolean;
import com.andrew.game.interfaces.MyCallbackEmpty;
import com.andrew.game.slip.Slip;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


public class SpriteShopScreen extends BaseMenuScreen {

    private final TextureAtlas textureAtlas;
    private final SpriteBatch spriteBatch;
    private DailyReward dailyReward;
    private Table layoutTable;
    private Offer basic, rare, epic;

    private enum OfferValue {BASIC, RARE, EPIC}

    public SpriteShopScreen(Slip game) {
        super(game);
        textureAtlas = new TextureAtlas("sprites.txt");
        spriteBatch = new SpriteBatch();
    }

    @Override
    public void show() {

        super.show();
        dailyReward = new DailyReward(game, mySkin, new MyCallbackBoolean() {
            @Override
            public void onCallback(boolean status) {
                buttonClickedSound();
                System.out.println("called back" + status);
                if (status) {
                    clickedDaily();
                } else {
                    dailyLocked();
                }
            }
        });
        basic = new Offer("Basic\nGatcha", OfferValue.BASIC);
        rare = new Offer("Rare\nGatcha", OfferValue.RARE);
        epic = new Offer("Epic\nGatcha", OfferValue.EPIC);
        layoutTable = new Table();
        layoutTable.defaults().space(BaseMenuScreen.DEFAULT_UNIT).uniform().grow();
        layoutTable.pad(BaseMenuScreen.DEFAULT_UNIT);
        layoutTable.add(
                getDailyRewardTable(),
                basic, rare, epic

        );

        topParent.add(layoutTable).grow().colspan(TOP_PARENT_COLUMN);
        Gdx.input.setInputProcessor(stage);
    }


    private Table getDailyRewardTable() {
        Table table = getTable();
        table.add(getTitle("Daily\nReward")).row();
        //table.add().growY().row();
        table.add(dailyReward.getDailyRewardTable()).grow();
        return table;
    }

    @Override
    protected TextButton getSpriteShopButton(Skin mySkin) {
        TextButton textButton = new TextButton("Sprite Shop", mySkin, "gold");
        addListener(textButton, NavigationEvent.SPRITE_SHOP);
        return textButton;
    }

    private Table getTable() {
        return new Table().background(mySkin.getDrawable("rewardTableBackground")).pad(BaseMenuScreen.DEFAULT_UNIT);
    }

    private ImageButton getPresent() {
        return new ImageButton(mySkin, "present");
    }


    private Label getTitle(String title) {
        Label label = new Label(title, new Label.LabelStyle(assets.buttonLightFont, Color.valueOf("280792")));
        label.setAlignment(Align.center);
        return label;
    }

    private Table getPriceTable(int cost) {
        Table table = new Table();
        Image image = new Image(mySkin.getDrawable("crystal1"));
        Label label = new Label(String.valueOf(cost), new Label.LabelStyle(assets.buttonLightFont, Color.WHITE));
        image.setScaling(Scaling.fit);
        table.add(image);
        table.add(label);
        return table;
    }

    private Button getPrice(int cost) {
        Button button = new Button(mySkin);
        button.add(getPriceTable(cost));
        return button;
    }

    @Override
    protected void switchScreen(NavigationEvent navigationEvent) {
        if (navigationEvent == NavigationEvent.SPRITE_SHOP) return;
        super.switchScreen(navigationEvent);
    }

    @Override
    public void render(float delta) {
        dailyReward.update(delta);
        super.render(delta);
    }

    @Override
    public void dispose() {
        super.dispose();
        textureAtlas.dispose();
        spriteBatch.dispose();
    }
    //todo daily reward system is very clunky as is, clean it up and make it smarter

    private void clickedDaily() {
        System.out.println("daily reward clicked");

        new DailyRewardWindow(game, mySkin, new MyCallbackEmpty() {
            @Override
            public void onCallback() {
                final Dialog dialog = new Dialog("", mySkin, "test");
                final ImageButton imageButton = new ImageButton(mySkin, "present");
                imageButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        imageButton.setDisabled(true);
                        dialog.hide();
                        System.out.println("opened present");
                        int rewardValue = ThreadLocalRandom.current().nextInt(30, 140);
                        game.accountManager.addCoins(rewardValue);
                        game.fireBaseAnalytics.logEarnVirtualCurrency(rewardValue, "coins");
                        dailyReward.redeemedDailyReward();
                        TapAnyWhereCloseDialog tapCloseDialog = new TapAnyWhereCloseDialog("", mySkin);
                        tapCloseDialog.getContentTable().add(getPriceTable(rewardValue)).pad(BaseMenuScreen.DEFAULT_UNIT);

                        tapCloseDialog.show(stage);
                    }
                });
                imageButton.getImageCell().grow();
                dialog.getContentTable().add(imageButton).size(130 * Gdx.graphics.getDensity()).grow();
                dialog.show(stage);
            }
        }).show(stage);
    }

    private void dailyLocked() {
        new TapCloseDialog("", mySkin).text("Time until next reward:  ").text(dailyReward.getTimeRemaining()).show(stage);
    }

    private class Offer extends Table {

        private final ArrayList<Integer> fullArray;

        //todo change the hardcoded values into something new. and more dynamic
        private ArrayList<Integer> spriteIDs; //determined when the user presses the present button

        private Map<Integer, Image> imageMap; //holds a reference to the images in the table. key is the spriteID Array's object (the sprites actual ID).
        private Label amountCollected; //holds a reference to the number label representing the amount collected
        private int spritesCollected; //holds the reference to the number used in amountCollected. gets incremented when the user purchases a sprite, before the label gets updated

        private final int cost;

        public Offer(String title, OfferValue offerValue) {

            imageMap = new HashMap<>();
            switch (offerValue) {
                //todo change integers in arrays.asList when we figure out how much each sprite is going to cost
                case BASIC:
                    cost = 1000;
                    fullArray = new ArrayList<>(Arrays.asList(1, 2, 4, 5));
                    break;
                case RARE:
                    cost = 2000;
                    fullArray = new ArrayList<>(Arrays.asList(3, 6, 9, 11));
                    break;
                case EPIC:
                    cost = 5000;
                    fullArray = new ArrayList<>(Arrays.asList(7, 8, 10, 18));
                    break;
                default:
                    throw new UnsupportedOperationException(String.format(Locale.US, "%s is not a valid offerValue", offerValue));
            }
            pad(BaseMenuScreen.DEFAULT_UNIT);
            setBackground(mySkin.getDrawable("gatchaTableBackground"));
            add(getTitle(title)).row();
            add(getPossibilities()).grow().row();
            add(getPrice()).growX();
        }

        private ArrayList<Integer> getPossibleSprites(ArrayList<Integer> allSpritesOfRarity) {
            allSpritesOfRarity.removeAll(game.accountManager.getAccountObject().getSprites());
            return allSpritesOfRarity;
        }

        private Table getPossibilities() {

            Table possibleRewardTable = new Table();
            //possibleRewardTable.top();
            possibleRewardTable.defaults().space(BaseMenuScreen.DEFAULT_UNIT).top();
            spritesCollected = 0;
            int i = 0;
            for (int x : fullArray) {
                if (i % 2 == 0) {
                    possibleRewardTable.row();
                }

                Image image = new Image(new TextureRegionDrawable(textureAtlas.findRegion(x + "-down")));
                image.setScaling(Scaling.fit);

                //key: the sprite's ID
                //Image: the image added to the table
                imageMap.put(x, image);

                //either adds increments spritesCollectedCount or dims the sprite to show that it hasn't been unlocked yet
                if (game.accountManager.getAccountObject().getSprites().contains(x)) {
                    spritesCollected++;
                } else {
                    image.addAction(Actions.alpha(.3f));
                }

                possibleRewardTable.add(image).size(70.71428571f * Gdx.graphics.getDensity()).growX();
                i++;
            }

            ScrollPane scrollPane = new ScrollPane(possibleRewardTable);
            scrollPane.setScrollingDisabled(true, false);
            Table container = new Table();

            Table labelContainer = new Table();
            Label collected1, collected3;
            collected1 = new Label("Collected: ", new Label.LabelStyle(assets.textFont, Color.DARK_GRAY));
            amountCollected = new Label(String.valueOf(spritesCollected), new Label.LabelStyle(assets.textFont, Color.PURPLE));
            collected3 = new Label(" /" + fullArray.size(), new Label.LabelStyle(assets.textFont, Color.DARK_GRAY));
            labelContainer.add(collected1, amountCollected, collected3);

            container.add(labelContainer).row();
            container.add(scrollPane).grow();
            return container;
        }

        private Button getPrice() {
            Button button = SpriteShopScreen.this.getPrice(cost);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    buttonClickedSound();
                    if (cost > game.accountManager.getAccountObject().getCoins()) {
                        new TapAnyWhereCloseDialog("", mySkin).text("not enough crystals").show(stage);
                        return;
                    }
                    spriteIDs = getPossibleSprites(fullArray);
                    if (spriteIDs.size() == 0) {
                        new TapAnyWhereCloseDialog("", mySkin).text("All Characters have been collected").show(stage);
                        return;
                    }
                    confirm(cost);
                }
            });
            return button;
        }

        private void confirm(final int cost) {
            Dialog confirmationDialog = new Dialog("", mySkin);
            confirmationDialog.pad(DEFAULT_UNIT);
            Label label = new Label("Spend ", new Label.LabelStyle(assets.buttonLightFont, Color.WHITE));
            confirmationDialog.getContentTable().add(label, getPriceTable(cost)).pad(DEFAULT_UNIT);
            TextButton yes = new TextButton("Yes", mySkin);
            yes.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    openPresent(cost);
                }
            });
            confirmationDialog.getButtonTable().defaults().pad(DEFAULT_UNIT).grow();
            confirmationDialog.button("No");
            confirmationDialog.button(yes);
            confirmationDialog.show(stage);

        }

        private void openPresent(int cost) {

            final int index = ThreadLocalRandom.current().nextInt(0, spriteIDs.size());
            Dialog dialog = new Dialog("", mySkin);
            ImageButton imageButton = getPresent();
            final Image image = new Image(new SpriteDrawable(textureAtlas.createSprite(spriteIDs.get(index) + "-down")));
            game.accountManager.unlockSprite(spriteIDs.get(index), cost);
            spritesCollected++; //increments the number of sprites collected. separate from the original calculation to reduce amount of unnecessary reinitialization

            dialog.text("Tap to Open!");
            dialog.getContentTable().row();
            dialog.button(imageButton);
            imageButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    //todo refresh shop table, to reflect newly acquired sprite
                    TapAnyWhereCloseDialog tapCloseDialog = new TapAnyWhereCloseDialog("", mySkin, "test", new MyCallbackEmpty() {
                        @Override
                        public void onCallback() {
                            amountCollected.setText(String.valueOf(spritesCollected)); //updates the number of sprites collected in the table
                            imageMap.get(spriteIDs.get(index)).addAction(Actions.alpha(1)); //fades in a faded out sprite
                        }
                    }) //parameters end
                    {

                        @Override
                        public float getPrefWidth() {
                            return Gdx.graphics.getWidth() * .25f;
                        }

                        @Override
                        public float getPrefHeight() {
                            return Gdx.graphics.getWidth() * .25f;
                        }
                    };
                    tapCloseDialog.getContentTable().add(image).size(Gdx.graphics.getWidth() * .25f);
                    tapCloseDialog.show(stage);
                }
            });
            dialog.show(stage);

        }


    }


}
