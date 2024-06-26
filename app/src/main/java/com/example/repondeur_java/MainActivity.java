package com.example.repondeur_java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.repondeur_java.databinding.ActivityMainBinding;
import com.example.repondeur_java.entities.Contact;
import com.example.repondeur_java.entities.Response;
import com.example.repondeur_java.fragments.ContactsFragment;
import com.example.repondeur_java.fragments.MessageFragment;
import com.example.repondeur_java.fragments.SendFragment;
import com.example.repondeur_java.utils.ResponsesManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Contact> contactsList = new ArrayList<>();
    private ArrayList<Response> responsesList = new ArrayList<>();
    private ArrayList<Contact> selectedContacts = new ArrayList<>();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new ContactsFragment());

        // Gestion de la navigation entre les 3 fragments (Contacts, Message, Send)
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.contacts:
                    replaceFragment(new ContactsFragment());
                    return true;

                case R.id.messages:
                    replaceFragment(new MessageFragment());
                    return true;

                case R.id.send:
                    replaceFragment(new SendFragment());
                    return true;
            }
            return false;
        });
    }

    /**************************
     * Gestion des fragments
    **************************/
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    /**************************
     * Contacts sélectionnés
    **************************/
    public ArrayList<Contact> getSelectedContacts() {
        return selectedContacts;
    }

    public void setSelectedContacts(ArrayList<Contact> selectedContacts) {
        this.selectedContacts = selectedContacts;
    }

    /**************************
     * Contact liste
    **************************/
    public ArrayList<Contact> getContactsList() {
        return contactsList;
    }

    public void setContactsList(ArrayList<Contact> contacts) {
        this.contactsList = contacts;
    }

    /**************************
     * Réponses liste
    **************************/
    public ArrayList<Response> getResponsesList() {
        return responsesList;
    }

    public void setResponsesList(ArrayList<Response> responses) {
        this.responsesList = responses;
    }


    /**************************
     * Réponses sélectionnées
    **************************/
    public Response getSpamResponse() {
        // Récupération des réponses
        ArrayList<Response> responses = ResponsesManager.getResponses(this);

        // Recherche de la réponse cochée comme spam
        for (Response response : responses) {
            if (response.isSpam()) {
                return response;
            }
        }
        return null;
    }

    public Response getAutoResponse() {
        // Récupération des réponses
        ArrayList<Response> responses = ResponsesManager.getResponses(this);

        // Recherche de la réponse cochée comme réponse automatique
        for (Response response : responses) {
            if (response.isAutomaticResponse()) {
                return response;
            }
        }
        return null;
    }
}
