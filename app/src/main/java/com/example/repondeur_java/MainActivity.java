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

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new ContactsFragment());

        // Gestion de la navigation entre les fragments
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

    // Gestion des fragments
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Remplacement du fragment actuel par le nouveau dans le FrameLayout
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
