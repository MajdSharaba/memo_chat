package com.yawar.memo.sessionManager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yawar.memo.model.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

import javax.inject.Singleton;

public class ClassSharedPreferences {
    Context context;

    public ClassSharedPreferences(Context context) {
        this.context = context;
    }


    public void setUser(UserModel user) {
        SharedPreferences prefs = context.getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        prefsEditor.putString("UserModel", json).commit();
    }

    public UserModel getUser() {
        SharedPreferences prefs = context.getSharedPreferences("user", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = prefs.getString("UserModel", "");
        UserModel user = gson.fromJson(json, UserModel.class);
        return user;

    }

    public void setSecretNumbers(JSONObject secretNumbers) {
        SharedPreferences prefs = context.getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(secretNumbers);
        prefsEditor.putString("SecretNumbers", secretNumbers.toString()).commit();

    }


    public JSONObject getSecretNumbers() {
        JSONObject response = new JSONObject();
        SharedPreferences prefs = context.getSharedPreferences("user", MODE_PRIVATE);

        Gson gson = new Gson();
//        String json = prefs.getString("SecretNumbers", "");
//        JSONObject user = gson.fromJson(json);
        String json = prefs.getString("SecretNumbers", "");
        if (json != null) {
            try {
                response = new JSONObject(json);

            } catch (JSONException e) {

            }
        }

        return response;

    }

    public void setName(String name) {
        SharedPreferences prefs = context.getSharedPreferences("profile", MODE_PRIVATE);
        prefs.edit().putString("name", name).commit();
        System.out.println("Memo+" + name);

    }

    public void setVerficationNumber(String number) {
        SharedPreferences prefs = context.getSharedPreferences("profile", MODE_PRIVATE);
        prefs.edit().putString("verficationNumber", number).commit();

    }

    public void setNumber(String number) {
        SharedPreferences prefs = context.getSharedPreferences("profile", MODE_PRIVATE);
        prefs.edit().putString("number", number).commit();

    }

    public String getName() {
        SharedPreferences prefs = context.getSharedPreferences("profile", MODE_PRIVATE);

        String name = prefs.getString("name", "UserName");
        return name;


    }

    public String getVerficationNumber() {
        SharedPreferences prefs = context.getSharedPreferences("profile", MODE_PRIVATE);

        String number = prefs.getString("verficationNumber", null);
        return number;

    }

    public String getNumber() {
        SharedPreferences prefs = context.getSharedPreferences("profile", MODE_PRIVATE);

        String number = prefs.getString("number", "UserName");
        return number;

    }

//    public void setLocale(String lan) {
//        SharedPreferences prefs = context.getSharedPreferences("language", MODE_PRIVATE);
//
//        prefs.edit().putString("lan", lan).commit();
//
//
//    }

//    public String getLocale() {
//
//        SharedPreferences prefs = context.getSharedPreferences("language", MODE_PRIVATE);
//
//
//        String lan = prefs.getString("lan", "ar");
//        return lan;
//
//    }

    public <T> void setList(String key, List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);

        set(key, json);
    }

    public void set(String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("list", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public ArrayList<JSONObject> getList() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("list", Context.MODE_PRIVATE);

        ArrayList<JSONObject> arrayItems = null;
        String serializedObject = sharedPreferences.getString("list", null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<JSONObject>>() {
            }.getType();
            arrayItems = gson.fromJson(serializedObject, type);
        }
return arrayItems;

    }
    public void setFcmToken(String token) {
        System.out.println("set fcm token"+token);
        SharedPreferences prefs = context.getSharedPreferences("profile", MODE_PRIVATE);
        prefs.edit().putString("token", token).commit();
        System.out.println("Memo+" + token);}
    public String getFcmToken() {
        SharedPreferences prefs = context.getSharedPreferences("profile", MODE_PRIVATE);

        String token = prefs.getString("token", "empty");
        return token;


    }




//        prefs.edit().p("user",user).commit();



}
