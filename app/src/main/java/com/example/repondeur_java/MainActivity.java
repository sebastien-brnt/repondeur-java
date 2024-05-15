package com.example.repondeur_java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.repondeur_java.databinding.ActivityMainBinding;
import com.example.repondeur_java.fragments.ContactsFragment;
import com.example.repondeur_java.fragments.MessageFragment;
import com.example.repondeur_java.fragments.SendFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Contact> selectedContacts = new ArrayList<>();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new ContactsFragment());

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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public ArrayList<Contact> getSelectedContacts() {
        return selectedContacts;
    }

    public void setSelectedContacts(ArrayList<Contact> selectedContacts) {
        this.selectedContacts = selectedContacts;
    }
}
