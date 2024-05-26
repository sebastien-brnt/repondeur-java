package com.example.repondeur_java.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.repondeur_java.elements.Contact;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.Phonenumber;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsReceiver";
    private ArrayList<Contact> selectedContacts;
    private String autoResponseMessage;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            // Récupérer les contacts et le message de réponse automatique des SharedPreferences
            retrieveSelectedContacts(context);
            retrieveAutoResponseMessage(context);

            Log.d(TAG, "Message received");
            Log.d(TAG, "Selected contacts: " + selectedContacts);

            // Récupération des informations du message reçu
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], bundle.getString("format"));
                    String sender = messages[i].getOriginatingAddress();

                    Log.d(TAG, "Message from: " + sender);

                    if (isContactSelected(context, sender)) {
                        Log.d(TAG, "Sender is a selected contact. Sending auto-response.");
                        sendAutoResponse(context, sender);
                    } else {
                        Log.d(TAG, "Sender is not a selected contact.");
                    }
                }
            }
        }
    }

    private void retrieveSelectedContacts(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AutoResponsePrefs", Context.MODE_PRIVATE);

        String json = sharedPreferences.getString("automaticContacts", null);
        Log.d("RetrieveContacts", "JSON récupéré: " + json);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Contact>>() {}.getType();
            selectedContacts = gson.fromJson(json, type);
            Log.d("RetrieveContacts", "Contacts récupérés: " + selectedContacts);
        } else {
            Log.d("RetrieveContacts", "Aucun contact trouvé dans SharedPreferences.");
            selectedContacts = new ArrayList<>();
        }
    }



    private void retrieveAutoResponseMessage(Context context) {
        // Récupération de la réponse automatique enregistrée dans SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("AutoResponsePrefs", Context.MODE_PRIVATE);
        autoResponseMessage = sharedPreferences.getString("autoResponseMessage", null);
    }

    private boolean isContactSelected(Context context, String sender) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber senderNumber = phoneUtil.parse(sender, Locale.getDefault().getCountry());
            String normalizedSender = phoneUtil.format(senderNumber, PhoneNumberUtil.PhoneNumberFormat.E164);

            if (selectedContacts != null) {
                for (Contact contact : selectedContacts) {
                    Phonenumber.PhoneNumber contactNumber = phoneUtil.parse(contact.getPhoneNumber(), Locale.getDefault().getCountry());
                    String normalizedContact = phoneUtil.format(contactNumber, PhoneNumberUtil.PhoneNumberFormat.E164);

                    if (normalizedContact.equals(normalizedSender)) {
                        return true;
                    }
                }
            }
        } catch (NumberParseException e) {
            Log.e(TAG, "NumberParseException was thrown: " + e.toString());
        }

        return false;
    }

    private void sendAutoResponse(Context context, String phoneNumber) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, autoResponseMessage, null, null);
            Log.d(TAG, "Auto response sent to: " + phoneNumber);
        } catch (Exception e) {
            Log.e(TAG, "Failed to send auto response.", e);
        }
    }
}
