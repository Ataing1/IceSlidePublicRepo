package com.andrew.game.interfaces;

import java.util.Map;

/**
 * used to grab information about the logged in User.
 * NOT FOR LOADING ACCOUNT OBJECT
 */
public interface MyCallbackUserInfo {

    void onCallback(Map<String, Object> map);

}
