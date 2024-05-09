package com.example.repondeur_java.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.repondeur_java.Contact;
import com.example.repondeur_java.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private EditText inputResponse;
    private RadioGroup radioGroup;

    public MessageFragment() {
        // Required empty public constructor
    }

    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
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
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        inputResponse = view.findViewById(R.id.input_response);
        radioGroup = view.findViewById(R.id.radio_group_responses);
        Button addButton = view.findViewById(R.id.add_response_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCustomResponse();
            }
        });

        addResponseFromPhoneMemory();

        return view;
    }

    private void addCustomResponse() {
        String customResponse = inputResponse.getText().toString().trim();

        if (!TextUtils.isEmpty(customResponse)) {
            RadioButton radioButton = new RadioButton(requireContext());
            radioButton.setText(customResponse);
            radioGroup.addView(radioButton);
            saveResponses(customResponse);
            inputResponse.setText("");
        } else {
            Toast.makeText(requireContext(), "Veuillez entrer une réponse", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveResponses(String response) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserResponses", Context.MODE_PRIVATE);
        Set<String> responsesSet = getSavedResponses();
        responsesSet.add(response);
        sharedPreferences.edit().putStringSet("responses", responsesSet).apply();
    }

    private Set<String> getSavedResponses() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserResponses", Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet("responses", new HashSet<String>());
    }

    private void addResponseFromPhoneMemory() {
        Set<String> savedResponses = getSavedResponses();
        for (String response : savedResponses) {
            RadioButton radioButton = new RadioButton(requireContext());
            radioButton.setText(response);
            radioGroup.addView(radioButton);
        }
    }

    private void clearSavedResponses() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserResponses", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    private void hydrateContactListTextView(List<Contact> contacts) {
        TextView contactListText = requireView().findViewById(R.id.contact_list);
        StringBuilder contactList = new StringBuilder();
        for (Contact contact : contacts) {
            contactList.append(contact.getName());
            if (contacts.indexOf(contact) != contacts.size() - 1) {
                contactList.append(", ");
            }
        }
        contactListText.append(contactList.toString());
    }
}
