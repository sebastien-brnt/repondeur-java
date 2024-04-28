package com.example.repondeur_java;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectResponseActivity extends AppCompatActivity {
    private EditText inputResponse;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_response);

        // Récupération de l'EditText, du RadioGroup et du Button
        inputResponse = findViewById(R.id.input_response);
        radioGroup = findViewById(R.id.response_radio_group);
        Button addButton = findViewById(R.id.add_response_button);

        // Ajout d'un écouteur au click sur le bouton pour ajouter une réponse personnalisée
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCustomResponse();
            }
        });

        addResponseFromPhoneMemory();
    }

    private void addCustomResponse() {
        // Récupération du texte de l'EditText
        String customResponse = inputResponse.getText().toString().trim();

        // Vérification que le texte n'est pas vide
        if (!TextUtils.isEmpty(customResponse)) {
            // Création et ajout d'un nouveau RadioButton avec le texte de l'EditText
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(customResponse);
            radioGroup.addView(radioButton);

            // Sauvegarde de la réponse personnalisée la mémoire du téléphone
            saveResponses(customResponse);

            // Suppression du texte de l'EditText après l'ajout
            inputResponse.setText("");
        } else {
            // Affichage d'un toast si le texte est vide
            Toast.makeText(this, "Veuillez entrer une réponse", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveResponses(String response) {
        // Ajout de la réponse à la liste des réponses sauvegardées
        SharedPreferences sharedPreferences = getSharedPreferences("UserResponses", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> responsesSet = getSavedResponses();
        responsesSet.add(response);
        editor.putStringSet("responses", responsesSet);
        editor.apply();
    }

    private Set<String> getSavedResponses() {
        // Récupération des réponses sauvegardées
        SharedPreferences sharedPreferences = getSharedPreferences("UserResponses", Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet("responses", new HashSet<String>());
    }

    private void addResponseFromPhoneMemory() {
        // Récupération du RadioGroup
        RadioGroup radioGroup = findViewById(R.id.response_radio_group);

        // Récupération des réponses sauvegardées
        Set<String> savedResponses = getSavedResponses();

        // Ajout des boutons radios pour chaque réponse sauvegardée
        for (String response : savedResponses) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(response);
            radioGroup.addView(radioButton);
        }
    }

    private void clearSavedResponses() {
        // Suppression de toutes les réponses sauvegardées
        SharedPreferences sharedPreferences = getSharedPreferences("UserResponses", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}

