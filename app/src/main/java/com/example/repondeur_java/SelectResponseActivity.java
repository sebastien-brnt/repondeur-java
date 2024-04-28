package com.example.repondeur_java;

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

import java.util.List;

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

            // Suppression du texte de l'EditText après l'ajout
            inputResponse.setText("");
        } else {
            // Affichage d'un toast si le texte est vide
            Toast.makeText(this, "Veuillez entrer une réponse", Toast.LENGTH_SHORT).show();
        }
    }
}

