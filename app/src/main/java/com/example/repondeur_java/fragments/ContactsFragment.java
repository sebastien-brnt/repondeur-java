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

import com.example.repondeur_java.Contact;
import com.example.repondeur_java.ContactsRecyclerAdapter;
import com.example.repondeur_java.R;

import java.util.ArrayList;

public class ContactsFragment extends Fragment {

    private ArrayList<Contact> dataset = new ArrayList<>();
    private ContactsRecyclerAdapter adapter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ContactsFragment() {
        // Required empty public constructor
    }

    public static ContactsFragment newInstance(String param1, String param2) {
        ContactsFragment fragment = new ContactsFragment();
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
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        // Création du RecyckerView pour la liste des messages
        RecyclerView recyclerView = view.findViewById(R.id.contacts_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ContactsRecyclerAdapter(dataset);
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

    public boolean isPermissionsGranted() {
        // Retourne vrai si l'utilisateur a donné sa permission pour lire, envoyer des SMS et lire les contacts
        return ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissions() {
        // Liste des permissions nécessaires
        String[] permissions = {
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.READ_CONTACTS
        };

        // Demande à l'utilisateur les permissions nécessaires
        ActivityCompat.requestPermissions(requireActivity(), permissions, 0);
    }

    private void loadContacts() {
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

        // Mise à jour de l'adapter avec la liste des contacts récupérés
        adapter.updateContacts(contactList);
    }
}
