package com.example.repondeur_java.fragments;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.repondeur_java.entities.Contact;
import com.example.repondeur_java.MainActivity;
import com.example.repondeur_java.R;
import com.example.repondeur_java.entities.Response;
import com.example.repondeur_java.recyclerAdapters.ContactsRecyclerAdapter;
import com.example.repondeur_java.utils.SmsReceiver;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class SendFragment extends Fragment {

    private static final int SMS_PERMISSION_CODE = 123;
    private static final int REQUEST_SCHEDULE_EXACT_ALARM_PERMISSION_CODE = 124;
    private Spinner contactsSpinner;
    private Spinner contactsSpinnerSchedule;
    private EditText messageInput;
    private TimePicker timePicker;
    private ArrayAdapter<String> spinnerAdapter;
    private ArrayList<Contact> selectedContacts;
    private ArrayList<Contact> recipients;
    private ContactsRecyclerAdapter adapter;
    private ArrayList<Contact> dataset = new ArrayList<>();


    public SendFragment() {
        // Constructeur public vide requis
    }

    /**************************
     * Création de la vue
    **************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send, container, false);

        // Récupération des boutons du fragment
        Button sendSpamButton = view.findViewById(R.id.send_spam);
        Button automaticResponseButton = view.findViewById(R.id.send_automatic_response);
        Button sendScheduledMessageButton = view.findViewById(R.id.send_scheduled_message);

        // Récupération du TimePicker et du message
        timePicker = view.findViewById(R.id.time_picker);
        messageInput = view.findViewById(R.id.message_input);

        // Initialise les spinner
        contactsSpinner = view.findViewById(R.id.contacts_spinner);
        contactsSpinnerSchedule = view.findViewById(R.id.contacts_spinner_schedule);

        // Initialise les adaptateurs du spinner avec une liste vide
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contactsSpinner.setAdapter(spinnerAdapter);
        contactsSpinnerSchedule.setAdapter(spinnerAdapter);

        // Récupère les contacts sélectionnés dans la section "Contacts"
        selectedContacts = ((com.example.repondeur_java.MainActivity) getActivity()).getSelectedContacts();

        if (selectedContacts != null && !selectedContacts.isEmpty()) {
            // Met à jour les contacts sélectionnés dans les spinners
            updateSpinnerItems(selectedContacts);
        } else {
            // Item par défaut si aucun contact n'est sélectionné
            spinnerAdapter.add("Aucun contact sélectionné");
        }

        // Récupération du titre de la liste des contacts
        TextView contactsTitle = view.findViewById(R.id.contacts_list_title);

        // Création du RecyclerView pour la liste des contacts qui reçoivent actuellement la réponse automatique
        RecyclerView recyclerView = view.findViewById(R.id.contacts_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Récupération des contacts dans le SharedPreferences
        recipients = getAutomaticResponseRecipients();

        if (recipients != null && !recipients.isEmpty()) {
            // Met à jour le titre de la liste des contacts
            contactsTitle.setText("Liste des contacts qui reçoivent actuellement la réponse automatique :");
            automaticResponseButton.setText("Désactiver la réponse automatique");

            // Met à jour la liste des contacts
            dataset.addAll(recipients);
        }

        adapter = new ContactsRecyclerAdapter(dataset, recipients, false);
        recyclerView.setAdapter(adapter);

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

                if ( recipients == null || recipients.isEmpty()) {
                    // Vérification si il y a des contacts sélectionnés depuis la section "Contacts"
                    if (selectedContacts == null || selectedContacts.isEmpty()) {
                        Toast.makeText(getContext(), "Aucun contact sélectionné", Toast.LENGTH_SHORT).show();

                    } else {
                        // Récupération de la réponse automatique
                        Response autoResponse = ((MainActivity) getActivity()).getAutoResponse();

                        // Vérification si une réponse automatique est trouvée
                        if (autoResponse != null) {

                            // Enregistrer les contacts sélectionnés dans la section "Contacts" et la réponse automatique définie dans les SharedPreferences
                            saveAutomaticContacts(selectedContacts);
                            saveAutoResponseMessage(autoResponse.getText());
                            Toast.makeText(getContext(), "Réponse automatique activée pour les contacts sélectionnés.", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getContext(), "Aucune réponse automatique trouvée", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // Suppression des contacts sélectionnés et de la réponse automatique enregistrés dans les SharedPreferences
                    saveAutomaticContacts(new ArrayList<>());
                    saveAutoResponseMessage("");
                    Toast.makeText(getContext(), "Réponse automatique désactivée !.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Gestion du clic sur le bouton "Plannifier l'envoi du message
        sendScheduledMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Vérification des permissions
                checkAndRequestExactAlarmPermission();

                // Vérification si il y a des contacts sélectionnés depuis la section "Contacts"
                if (selectedContacts == null || selectedContacts.isEmpty()) {
                    Toast.makeText(getContext(), "Aucun contact sélectionné", Toast.LENGTH_SHORT).show();
                } else if (contactsSpinnerSchedule.getSelectedItemPosition() == 0) {
                    Toast.makeText(getContext(), "Sélection invalide", Toast.LENGTH_SHORT).show();
                } else {
                    // Récupération du numéro de téléphone du contact sélectionné
                    String phoneNumber = selectedContacts.get(contactsSpinnerSchedule.getSelectedItemPosition() - 1).getPhoneNumber();

                    if (phoneNumber == null || phoneNumber.isEmpty()) {
                        Toast.makeText(getContext(), "Numéro de téléphone invalide", Toast.LENGTH_SHORT).show();

                    } else if (messageInput != null && !messageInput.getText().toString().isEmpty()) {
                        // Récupération du message à envoyer
                        String message = messageInput.getText().toString();

                        // Planification de l'envoi du message
                        scheduleSms(phoneNumber, message);
                    } else {
                        Toast.makeText(getContext(), "Aucune message renseigné", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }


    /**************************
     * Spinners
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


    /****************************************
     * Sauvegarde des contacts et du message
    ****************************************/
    // Méthode pour enregistrer les contacts sélectionnés dans les SharedPreferences
    private void saveAutomaticContacts(ArrayList<Contact> selectedContacts) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AutoContactsPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(selectedContacts);
        editor.putString("automaticContacts", json);
        editor.apply();

        Log.d("SaveContacts", "Contacts enregistrés: " + json);
    }

    // Méthode pour enregistrer le message de réponse automatique dans les SharedPreferences
    private void saveAutoResponseMessage(String message) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AutoResponsePrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("automaticResponseMessage", message);
        editor.apply();
    }

    /****************************************
    * Récupération des contacts automatiques
    ****************************************/
    private ArrayList<Contact> getAutomaticResponseRecipients() {
        // Récupération des contacts sélectionnés enregistrés dans SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AutoContactsPrefs", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("automaticContacts", null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Contact>>() {}.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<Contact>();
        }
    }

    /****************************************
     * Planification de l'envoi du message
     ****************************************/
    // Méthode pour planifier l'envoi d'un SMS
    private void scheduleSms(String phoneNumber, String message) {
        // Vérification de la permission SCHEDULE_EXACT_ALARM
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Permission requise pour planifier des alarmes exactes.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Création d'un calendrier avec l'heure et la minute sélectionnées
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        calendar.set(Calendar.MINUTE, timePicker.getMinute());
        calendar.set(Calendar.SECOND, 0);

        // Création de l'intent pour envoyer le SMS planifié
        Intent intent = new Intent(getContext(), SmsReceiver.class);
        intent.setAction("com.example.repondeur_java.SEND_SCHEDULED_SMS");
        intent.putExtra("phoneNumber", phoneNumber);
        intent.putExtra("message", message);

        // Création du PendingIntent pour l'intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Planification de l'envoi du SMS
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(getContext(), "Message planifié pour " + calendar.getTime(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Erreur lors de la planification du message.", Toast.LENGTH_SHORT).show();
        }
    }


    // Méthode pour vérifier et demander la permission de SCHEDULE_EXACT_ALARM
    private void checkAndRequestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM}, REQUEST_SCHEDULE_EXACT_ALARM_PERMISSION_CODE);
            }
        }
    }
}