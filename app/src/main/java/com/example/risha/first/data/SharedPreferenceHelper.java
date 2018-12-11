package com.example.risha.first.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.risha.first.model.User;



public class SharedPreferenceHelper {
    private static SharedPreferenceHelper instance = null;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    private static String SHARE_USER_INFO = "userinfo";
    private static String SHARE_KEY_NAME = "name";
    private static String SHARE_KEY_EMAIL = "email";
    private static String SHARE_KEY_AVATA = "avata";
    private static String SHARE_KEY_UID = "uid";
    private static String SHARE_KEY_NUMBER = "number";
    private static String SHARE_KEY_LANGUAGE = "language";
    private static String SHARE_KEY_DOB = "dob";
    private static String SHARE_KEY_GENDER = "gender";

    private SharedPreferenceHelper() {}

    public static SharedPreferenceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferenceHelper();
            preferences = context.getSharedPreferences(SHARE_USER_INFO, Context.MODE_PRIVATE);
            editor = preferences.edit();
        }
        return instance;
    }

    public void saveUserInfo(User user) {
        editor.putString(SHARE_KEY_NAME, user.name);
        editor.putString(SHARE_KEY_EMAIL, user.email);
        editor.putString(SHARE_KEY_AVATA, user.avata);
        editor.putString(SHARE_KEY_UID, StaticConfig.UID);
        editor.putString(SHARE_KEY_NUMBER,user.number);
        editor.putString(SHARE_KEY_LANGUAGE,user.Native_Language);
        editor.putString(SHARE_KEY_DOB,user.dob);
        editor.putString(SHARE_KEY_GENDER,user.gender);
        editor.apply();
    }

    public User getUserInfo(){
        String userName = preferences.getString(SHARE_KEY_NAME, "");
        String email = preferences.getString(SHARE_KEY_EMAIL, "");
        String avatar = preferences.getString(SHARE_KEY_AVATA, "default");
        String number = preferences.getString(SHARE_KEY_NUMBER, "");
        String language = preferences.getString(SHARE_KEY_LANGUAGE,"en");
        String dob = preferences.getString(SHARE_KEY_DOB,"");
        String gender = preferences.getString(SHARE_KEY_GENDER,"");
        User user = new User();
        user.name = userName;
        user.email = email;
        user.avata = avatar;
        user.number = number;
        user.Native_Language = language;
        user.dob = dob;
        user.gender = gender;
        return user;
    }

    public void clearUserInfo(){
        editor.clear();
        editor.apply();
    }

    public String getUID(){
        return preferences.getString(SHARE_KEY_UID, "");
    }

}
