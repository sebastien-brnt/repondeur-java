package com.example.repondeur_java.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.repondeur_java.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Utils {

    private static final String PREFS_NAME = "responses_prefs";
    private static final String RESPONSES_KEY = "responses_key";

    // Sauvegarder la liste des réponses
    public static void saveResponses(Context context, ArrayList<Response> responseList) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(responseList);
        editor.putString(RESPONSES_KEY, json);
        editor.apply();
    }

    // Restaurer la liste des réponses
    public static ArrayList<Response> getResponses(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(RESPONSES_KEY, null);
        Type type = new TypeToken<ArrayList<Response>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
