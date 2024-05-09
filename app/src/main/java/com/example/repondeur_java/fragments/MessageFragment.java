package com.example.repondeur_java.fragments;

import android.content.Context;
import android.content.Intent;
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

import com.example.repondeur_java.R;
import com.example.repondeur_java.Response;
import com.example.repondeur_java.ResponsesRecyclerAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private EditText inputResponse;

    private ResponsesRecyclerAdapter adapter;

    public MessageFragment() {
        // Required empty public constructor
    }

    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        inputResponse = view.findViewById(R.id.input_response);
        Button addButton = view.findViewById(R.id.add_response_button);

        // Initialize adapter
        adapter = new ResponsesRecyclerAdapter(new ArrayList<>());

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



    private void addCustomResponse() {
        String customResponse = inputResponse.getText().toString().trim();

        if (!TextUtils.isEmpty(customResponse)) {
            saveResponse(customResponse);
            inputResponse.setText("");
        } else {
            Toast.makeText(requireContext(), "Veuillez entrer une réponse", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveResponse(String response) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserResponses", Context.MODE_PRIVATE);
        Set<String> responsesSet = getSavedResponses();
        responsesSet.add(response);
        sharedPreferences.edit().putStringSet("responses", responsesSet).apply();
    }

    private Set<String> getSavedResponses() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserResponses", Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet("responses", new HashSet<String>());
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
        // Initialisation de la liste des réponses
        List<Response> responsesList = new ArrayList<>();

        // Ajout des réponses prédéfinies
        responsesList.add(new Response("Je ne suis pas disponible pour le moment", false, false));
        responsesList.add(new Response("Pas disponible, veuillez me recontacter", false, false));
        responsesList.add(new Response("Bonjour à toi", false, false));
        responsesList.add(new Response("C'est un message de test !", false, false));
        responsesList.add(new Response("C'est un message qui sera automatique", false, false));

        // Mise à jour de l'adaptateur avec la liste des réponses récupérées
        adapter.updateResponses(responsesList);
    }
}
