package com.andrew.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AccountObject {

    private int coins;
    private boolean adFree;
    private final List<Integer> sprites;
    private int selectedSpriteID;
    private Date date;
    private Date timeTillNextLife;
    private int streak;
    private int lives;
    private int trophy;
    private String userID;
    private int currentMap;
    private String email;
    private String username;

    public AccountObject() {
        coins = 0;
        adFree = false;
        sprites = new ArrayList<>(Collections.singletonList(1));
        selectedSpriteID = 1;
        date = null;
        timeTillNextLife = null;
        streak = 0;
        lives = 5;
        trophy = 600;
        userID = null;
        email = null;
        username = null;

    }

    public void setSelectedSpriteID(int selectedSpriteID) {
        this.selectedSpriteID = selectedSpriteID;
    }

    public int getSelectedSpriteID() {
        return selectedSpriteID;
    }

    public void addCoins(int amount) {
        coins += amount;
    }

    public void subCoins(int amount) {
        coins -= amount;
    }

    public int getCoins() {
        return coins;
    }

    public boolean getAdFree() {
        return adFree;
    }

    public void setAdFree(boolean adFree) {
        this.adFree = adFree;
    }

    public List<Integer> getSprites() {
        return sprites;
    }

    public void setDailyRewardDate(Date date) {
        this.date = date;
    }

    public Date getDailyRewardDate() {
        return date;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public Date getTimeTillNextLife() {
        return timeTillNextLife;
    }

    public void setTimeTillNewLife(Date timeTillNextLife) {
        this.timeTillNextLife = timeTillNextLife;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getTrophy() {
        return trophy;
    }

    public void setTrophy(int trophy) {
        this.trophy = trophy;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(int currentMap) {
        this.currentMap = currentMap;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "AccountObject{" +
                "coins=" + coins +
                ", adFree=" + adFree +
                ", sprites=" + sprites +
                ", selectedSpriteID=" + selectedSpriteID +
                ", date=" + date +
                ", timeTillNextLife=" + timeTillNextLife +
                ", streak=" + streak +
                ", lives=" + lives +
                ", trophy=" + trophy +
                ", userID='" + userID + '\'' +
                ", currentMap=" + currentMap +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
