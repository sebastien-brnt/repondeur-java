package com.example.repondeur_java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Contact> dataset = new ArrayList<>();
    ContactsRecyclerAdapter adapter = new ContactsRecyclerAdapter(dataset);

    public boolean isPermissionsGranted() {
        // Retourne vrai si l'utilisateur a donné sa permission pour lire, envoyer des SMS et lire les contacts
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissions() {
        // Liste des permissions nécessaires
        String[] permissions = {
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.READ_CONTACTS
        };

        // Demande à l'utilisateur les permissions
        ActivityCompat.requestPermissions(this, permissions, 0);
    }

    private void loadContacts() {
        // Initialisation de la liste des contacts
        ArrayList<Contact> contactList = new ArrayList<>();

        // Récupération des contacts
        ContentResolver resolver = getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = resolver.query(uri, null, null, null, null);

        // Parcours des contacts
        if (cursor != null && cursor.moveToFirst()) {
            // Récupération des index des colonnes
            int columnNameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int columnNumberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            do {
                // Récupération du nom et du numéro de téléphone
                String name = cursor.getString(columnNameIndex);
                String phoneNumber = cursor.getString(columnNumberIndex);

                // Création du contact et ajout à la liste
                Contact contact = new Contact(name, phoneNumber);
                contactList.add(contact);
            } while (cursor.moveToNext());

            cursor.close();
        }

        // Mise à jour de l'adapter avec la liste des contacts récupérés
        adapter.updateContacts(contactList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Récupération du bouton de passage à l'activité de réponse
        Button button = findViewById(R.id.select_response_button);

        // Création du RecyckerView pour la liste des messages
        RecyclerView rc_view = findViewById(R.id.contacts_recycler);
        rc_view.setLayoutManager(new LinearLayoutManager(this));
        rc_view.setAdapter(adapter);

        // Si les permissions ne sont pas accordées, on les demande, sinon on charge les contacts
        if (!isPermissionsGranted()) {
            requestPermissions();
        } else {
            // Chargement des contacts
            loadContacts();
        }

        // Passage à l'activité de réponse au clic sur le bouton
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupération des contacts sélectionnés
                ArrayList<Contact> contacts;
                contacts = adapter.getSelectedContacts();

                Intent intent = new Intent(MainActivity.this, SelectResponseActivity.class);

                // Envoi de la liste des contacts sélectionnés à l'activité suivante
                intent.putParcelableArrayListExtra("contactsList", contacts);
                startActivity(intent);

            }
        });





    }
}