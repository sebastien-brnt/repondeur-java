package com.example.repondeur_java.fragments;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.repondeur_java.elements.Contact;
import com.example.repondeur_java.recyclerAdapters.ContactsRecyclerAdapter;
import com.example.repondeur_java.MainActivity;
import com.example.repondeur_java.R;

import java.util.ArrayList;

public class ContactsFragment extends Fragment {

    private ArrayList<Contact> dataset = new ArrayList<>();
    private ArrayList<Contact> selectedContacts;
    private ArrayList<Contact> contactsList;
    private ContactsRecyclerAdapter adapter;

    public ContactsFragment() {
        // Constructeur public vide requis
    }

    /*****************************
     * Création de la vue
    *****************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        // Création du RecyclerView pour la liste des contacts
        RecyclerView recyclerView = view.findViewById(R.id.contacts_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        selectedContacts = ((MainActivity) requireActivity()).getSelectedContacts();
        contactsList = ((MainActivity) requireActivity()).getContactsList();
        adapter = new ContactsRecyclerAdapter(dataset, selectedContacts);
        recyclerView.setAdapter(adapter);

        // Si les permissions ne sont pas accordées, on les demande, sinon on charge les contacts
        if (!isPermissionsGranted()) {
            requestPermissions();
        } else {
            // Chargement des contacts
            loadContacts();
        }

        return view;
    }

    /*****************************
     * Gestion des permissions
    *****************************/
    public boolean isPermissionsGranted() {
        // Retourne vrai si l'utilisateur a donné sa permission pour lire les contacts
        return ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissions() {
        // Liste des permissions nécessaires
        String[] permissions = {
                android.Manifest.permission.READ_CONTACTS
        };

        // Demande à l'utilisateur les permissions nécessaires
        ActivityCompat.requestPermissions(requireActivity(), permissions, 0);
    }

    /*****************************
     * Gestion chargement contacts
    *****************************/
    private void loadContacts() {
        // Si la liste des contacts n'a pas été chargée, on la récupère
        if (contactsList.size() == 0) {
            ArrayList<Contact> contactList = new ArrayList<>();
            ContentResolver resolver = requireActivity().getContentResolver();
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            Cursor cursor = resolver.query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
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

            // Mise à jour de la liste des contacts
            ((com.example.repondeur_java.MainActivity) getActivity()).setContactsList(contactList);

            // Mise à jour de l'adapter avec la liste des contacts récupérés
            contactsList = ((com.example.repondeur_java.MainActivity) getActivity()).getContactsList();
            adapter.updateContacts(contactsList);
        } else {
            // Mise à jour de l'adapter avec la liste des contacts déjà chargés
            adapter.updateContacts(contactsList);
        }
    }
}
