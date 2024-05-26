package com.example.repondeur_java.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.repondeur_java.elements.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class ResponsesManager {

    private static final String PREFS_NAME = "responses_prefs";
    private static final String RESPONSES_KEY = "responses_key";


    /**************************************************
     * Gestion des réponses dans les SharedPreferences
     **************************************************/
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

        if (json == null) {
            // Retourne une liste vide si aucune donnée n'est trouvée
            return new ArrayList<>();
        }

        Type type = new TypeToken<ArrayList<Response>>() {}.getType();
        ArrayList<Response> responseList = gson.fromJson(json, type);

        if (responseList == null) {
            // Retourne une liste vide si la liste est nulle
            responseList = new ArrayList<>();
        }

        return responseList;
    }
}
