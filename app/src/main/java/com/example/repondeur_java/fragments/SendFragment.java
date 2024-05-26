package com.example.repondeur_java.fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import com.google.gson.Gson;

import java.util.ArrayList;

public class SendFragment extends Fragment {

    private static final int SMS_PERMISSION_CODE = 123;
    private Spinner contactsSpinner;
    private ArrayAdapter<String> spinnerAdapter;
    private ArrayList<Contact> selectedContacts;

    public SendFragment() {
        // Constructeur public vide requis
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

        // Récupère les contacts sélectionnés dans la section "Contacts"
        selectedContacts = ((com.example.repondeur_java.MainActivity) getActivity()).getSelectedContacts();

        if (selectedContacts != null && !selectedContacts.isEmpty()) {
            // Met à jour les contacts sélectionnés dans le spinner
            updateSpinnerItems(selectedContacts);
        } else {
            // Item par défaut si aucun contact n'est sélectionné
            spinnerAdapter.add("Aucun contact sélectionné");
        }

        // Récupération des boutons du fragment
        Button sendSpamButton = view.findViewById(R.id.send_spam);
        Button automaticResponseButton = view.findViewById(R.id.send_automatic_response);

        // Gestion du clic sur le bouton "Envoyer le message Spam"
        sendSpamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Vérification si il y a des contacts sélectionnés depuis la section "Contacts"
                if (selectedContacts == null || selectedContacts.isEmpty()) {
                    // Si aucun contact n'est sélectionné
                    Toast.makeText(getContext(), "Aucun contact sélectionné", Toast.LENGTH_SHORT).show();

                } else {
                    if (checkSMSPermissions()) {
                        // Récupération de la réponse cochée comme spam
                        Response spamResponse = ((MainActivity) getActivity()).getSpamResponse();

                        // Vérification si une réponse cochée comme spam est trouvée
                        if (spamResponse != null) {

                            // Vérification si un contact est sélectionné et non l'item de sélection par défaut
                            if ( contactsSpinner.getSelectedItemPosition() != 0) {

                                // Récupération du numéro de téléphone du contact sélectionné
                                String phoneNumber = selectedContacts.get(contactsSpinner.getSelectedItemPosition() - 1).getPhoneNumber();

                                // Récupération du message de la réponse cochée comme spam
                                String message = spamResponse.getText();

                                // Vérification du numéro de téléphone
                                if (phoneNumber == null || phoneNumber.isEmpty()) {
                                    Toast.makeText(getContext(), "Numéro de téléphone invalide", Toast.LENGTH_SHORT).show();

                                } else {
                                    // Envoi du message 4 fois
                                    for (int i = 0; i < 4; i++) {
                                        sendSMS(phoneNumber, message);
                                    }
                                }

                            } else {
                                // Si le contact n'est pas de type Contact
                                Toast.makeText(getContext(), "Sélection invalide", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Si aucune réponse cochée comme spam n'est trouvée
                            Toast.makeText(getContext(), "Aucune réponse marquée comme spam trouvée", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Si les permissions ne sont pas accordées, on les demandes
                        requestSMSPermissions();
                    }
                }
            }
        });


        // Gestion du clic sur le bouton "Activation réponse automatique"
        automaticResponseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Vérification si il y a des contacts sélectionnés depuis la section "Contacts"
                if (selectedContacts == null || selectedContacts.isEmpty()) {
                    Toast.makeText(getContext(), "Aucun contact sélectionné", Toast.LENGTH_SHORT).show();

                } else {
                    // Récupération de la réponse automatique
                    Response autoResponse = ((MainActivity) getActivity()).getAutoResponse();

                    // Vérification si une réponse automatique est trouvée
                    if (autoResponse != null) {

                        // Enregistrer les contacts sélectionnés dans la section "Contacts" et le message de réponse automatique dans les SharedPreferences
                        saveAutomaticContacts(selectedContacts);
                        saveAutoResponseMessage(autoResponse.getText());
                        Toast.makeText(getContext(), "Réponse automatique activée pour le contact sélectionné.", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getContext(), "Aucune réponse automatique trouvée", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }


    /**************************
     * Spinner
    **************************/
    // Méthode pour mettre à jour les contacts sélectionnés dans le spinner
    public void updateSpinnerItems(ArrayList<Contact> selectedContacts) {
        ArrayList<String> contactNames = new ArrayList<>();

        // Ajout d'un item par défaut
        contactNames.add("-- Sélectionnez un contact --");

        // Affichage pour chaque contact de son nom
        for (Contact contact : selectedContacts) {
            contactNames.add(contact.getName());
        }

        // Mise à jour de l'adaptateur du spinner
        spinnerAdapter.clear();
        spinnerAdapter.addAll(contactNames);
        spinnerAdapter.notifyDataSetChanged();
    }


    /**************************
     * Permissions
    **************************/
    // Méthode pour vérifier les permissions d'envoyer et de recevoir des SMS
    private boolean checkSMSPermissions() {
        boolean sendSMSPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
        boolean receiveSMSPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
        return sendSMSPermission && receiveSMSPermission;
    }


    // Méthode pour demander les permissions d'envoyer et de recevoir des SMS
    private void requestSMSPermissions() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS) || shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS)) {
            Toast.makeText(getContext(), "Les permissions d'envoyer et de recevoir des SMS sont nécessaires pour cette fonctionnalité.", Toast.LENGTH_SHORT).show();
        }
        requestPermissions(new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS}, SMS_PERMISSION_CODE);
    }


    // Méthode pour gérer la réponse de l'utilisateur à la demande de permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == SMS_PERMISSION_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                Toast.makeText(getContext(), "Permissions accordées. Vous pouvez maintenant envoyer et recevoir des SMS.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Permissions refusées. Vous ne pouvez pas envoyer ou recevoir des SMS.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**************************
     * Envoi SPAM
    **************************/
    // Méthode pour envoyer un SMS
    public void sendSMS(String phoneNumber, String message) {
        try {
            // Envoi du SMS
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getContext(), "SMS envoyé.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Échec de l'envoi du SMS.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    /**************************
     * Réponse automatique
    **************************/
    // Méthode pour enregistrer les contacts sélectionnés dans les SharedPreferences
    private void saveAutomaticContacts(ArrayList<Contact> selectedContacts) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AutoResponsePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(selectedContacts);
        editor.putString("automaticContacts", json);
        editor.apply();
    }



    // Méthode pour enregistrer le message de réponse automatique dans les SharedPreferences
    private void saveAutoResponseMessage(String message) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AutoResponsePrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("autoResponseMessage", message);
        editor.apply();
    }
}