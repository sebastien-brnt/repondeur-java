package com.example.repondeur_java.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.repondeur_java.Contact;
import com.example.repondeur_java.R;
import java.util.ArrayList;

public class SendFragment extends Fragment {

    private Spinner contactsSpinner;
    private ArrayAdapter<String> spinnerAdapter;

    public SendFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send, container, false);

        // Initialise le spinner
        contactsSpinner = view.findViewById(R.id.contacts_spinner);

        // Initialise l'adaptateur du spinner avec une liste vide
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contactsSpinner.setAdapter(spinnerAdapter);

        // Ajoute les contacts sélectionnés dans le spinner
        ArrayList<Contact> selectedContacts = ((com.example.repondeur_java.MainActivity) getActivity()).getSelectedContacts();
        if (selectedContacts != null && !selectedContacts.isEmpty()) {
            updateSelectedContacts(selectedContacts);
        } else {
            // Item par défaut
            spinnerAdapter.add("Aucun contact sélectionné");
        }

        // Configure les boutons
        Button sendSpamButton = view.findViewById(R.id.send_spam);
        Button automaticResponseButton = view.findViewById(R.id.send_automatic_response);

        // Gestion du clic sur le bouton "Envoyer spam"
        sendSpamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedContacts.isEmpty()) {
                    Toast.makeText(getContext(), "Aucun contact sélectionné", Toast.LENGTH_SHORT).show();
                } else {
                    // TODO: Action à effectuer lors du clic sur le bouton "Envoyer spam"
                }
            }
        });

        // Gestion du clic sur le bouton "Activation réponse automatique"
        automaticResponseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedContacts.isEmpty()) {
                    Toast.makeText(getContext(), "Aucun contact sélectionné", Toast.LENGTH_SHORT).show();
                } else {
                    // TODO: Action à effectuer lors du clic sur le bouton "Activation réponse automatique"
                }
            }
        });

        return view;
    }

    // Méthode pour mettre à jour les contacts sélectionnés dans le spinner
    public void updateSelectedContacts(ArrayList<Contact> selectedContacts) {
        ArrayList<String> contactNames = new ArrayList<>();
        for (Contact contact : selectedContacts) {
            contactNames.add(contact.getName());
        }

        spinnerAdapter.clear();
        spinnerAdapter.addAll(contactNames);
        spinnerAdapter.notifyDataSetChanged();
    }
}
