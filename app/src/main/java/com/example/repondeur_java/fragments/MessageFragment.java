package com.example.repondeur_java.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.repondeur_java.MainActivity;
import com.example.repondeur_java.R;
import com.example.repondeur_java.Response;
import com.example.repondeur_java.ResponsesRecyclerAdapter;
import com.example.repondeur_java.utils.UtilsMessage;

import java.util.ArrayList;

public class MessageFragment extends Fragment {

    private ArrayList<Response> responsesList;
    private EditText inputResponse;

    private ResponsesRecyclerAdapter adapter;

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        responsesList = ((MainActivity) requireActivity()).getResponsesList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        inputResponse = view.findViewById(R.id.input_response);
        Button addButton = view.findViewById(R.id.add_response_button);

        // Initialize adapter
        adapter = new ResponsesRecyclerAdapter(requireContext(), new ArrayList<>());

        RecyclerView recyclerView = view.findViewById(R.id.responses_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCustomResponse();
            }
        });

        addResponseFromPhoneMemory();

        return view;
    }


    // Ajout de la réponse personnalisée
    private void addCustomResponse() {
        String customResponse = inputResponse.getText().toString().trim();

        if (!TextUtils.isEmpty(customResponse)) {
            saveResponse(customResponse);
            inputResponse.setText("");
        } else {
            Toast.makeText(requireContext(), "Veuillez entrer une réponse", Toast.LENGTH_SHORT).show();
        }
    }

    // Sauvegarde de la réponse
    private void saveResponse(String response) {
        // Ajout de la réponse à la liste des réponses
        responsesList.add(new Response(response, false, false));
        ((MainActivity) requireActivity()).setResponsesList(responsesList);

        // Sauvegarder la liste des réponses dans SharedPreferences
        UtilsMessage.saveResponses(requireContext(), responsesList);

        // Mise à jour de l'adaptateur avec la nouvelle réponse
        this.loadResponses();
    }

    private ArrayList<Response> getSavedResponses() {
        return UtilsMessage.getResponses(requireContext());
    }

    private void addResponseFromPhoneMemory() {
        // Mise à jour des réponses depuis la mémoire du téléphone
        loadResponses();
    }

    private void clearSavedResponses() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserResponses", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    private void loadResponses() {
        // Récupération de la liste des réponses
        responsesList = UtilsMessage.getResponses(requireContext());

        // Si la liste des responses n'a pas été chargée, on la charge
        if (responsesList == null || responsesList.size() == 0) {
            // Initialisation de la liste des réponses
            ArrayList<Response> responses = new ArrayList<>();

            // Ajout des réponses prédéfinies
            responses.add(new Response("Je ne suis pas disponible pour le moment", false, false));
            responses.add(new Response("Pas disponible, veuillez me recontacter", false, false));
            responses.add(new Response("Bonjour à toi", false, false));
            responses.add(new Response("C'est un message de test !", false, false));
            responses.add(new Response("C'est un message qui sera automatique", false, false));

            // Mise à jour de la liste des réponses
            ((MainActivity) requireActivity()).setResponsesList(responses);
            responsesList = ((MainActivity) requireActivity()).getResponsesList();
            UtilsMessage.saveResponses(requireContext(), responsesList);

            adapter.updateResponses(responsesList);
        } else {
            // Mise à jour de l'adapter avec la liste des contacts déjà chargés
            adapter.updateResponses(responsesList);
        }
    }
}
