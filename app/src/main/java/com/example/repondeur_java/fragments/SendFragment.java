package com.example.repondeur_java.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.repondeur_java.Contact;
import com.example.repondeur_java.MainActivity;
import com.example.repondeur_java.R;
import com.example.repondeur_java.Response;

import java.util.ArrayList;

public class SendFragment extends Fragment {

    private static final int SMS_PERMISSION_CODE = 123;
    private Spinner contactsSpinner;
    private ArrayAdapter<String> spinnerAdapter;
    private ArrayList<Contact> selectedContacts;

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

        // Récupère les contacts sélectionnés
        selectedContacts = ((com.example.repondeur_java.MainActivity) getActivity()).getSelectedContacts();
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
                if (selectedContacts == null || selectedContacts.isEmpty()) {
                    Toast.makeText(getContext(), "Aucun contact sélectionné", Toast.LENGTH_SHORT).show();
                } else {
                    // Récupération de la réponse cochée comme spam
                    Response spamResponse = ((MainActivity) getActivity()).getSpamResponse();

                    // Si une réponse est cochée comme spam, on envoie un SMS au premier contact sélectionné
                    if (spamResponse != null) {
                        String phoneNumber = selectedContacts.get(0).getPhoneNumber(); // Ex: envoie au premier contact sélectionné
                        String message = spamResponse.getText();
                        if (checkSMSPermission()) {
                            // Envoi du message 4 fois
                            for (int i = 0; i < 4; i++) {
                                sendSMS(phoneNumber, message);
                            }
                        } else {
                            requestSMSPermission();
                        }
                    } else {
                        // Message d'erreur si aucune réponse n'est cochée comme spam
                        Toast.makeText(getContext(), "Aucune réponse marquée comme spam trouvée", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Gestion du clic sur le bouton "Activation réponse automatique"
        automaticResponseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedContacts == null || selectedContacts.isEmpty()) {
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

    // Méthode pour envoyer un SMS
    public void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getContext(), "SMS envoyé.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Échec de l'envoi du SMS.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    // Méthode pour vérifier la permission d'envoyer des SMS
    private boolean checkSMSPermission() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    // Méthode pour demander la permission d'envoyer des SMS
    private void requestSMSPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {
            Toast.makeText(getContext(), "La permission d'envoyer des SMS est nécessaire pour cette fonctionnalité.", Toast.LENGTH_SHORT).show();
        }
        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
    }

    // Méthode pour gérer la réponse de l'utilisateur à la demande de permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission accordée. Vous pouvez maintenant envoyer des SMS.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Permission refusée. Vous ne pouvez pas envoyer de SMS.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
