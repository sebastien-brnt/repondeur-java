package com.example.repondeur_java;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ContactsRecyclerAdapter extends RecyclerView.Adapter<ContactsRecyclerAdapter.ViewHolder> {
    private List<Contact> contactsDataset;
    private ArrayList<Contact> selectedContacts;

    public ContactsRecyclerAdapter(List<Contact> dataset) {
        contactsDataset = dataset;
        selectedContacts = new ArrayList<>();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nomTextView;
        private final TextView numTelTextView;
        private final CheckBox checkBox;

        ViewHolder(View v) {
            super(v);
            nomTextView = v.findViewById(R.id.contact_name);
            numTelTextView = v.findViewById(R.id.contact_phone_number);
            checkBox = v.findViewById(R.id.contact_checkbox);
        }

        TextView getNomTextView() {
            return nomTextView;
        }

        TextView getNumTelTextView() {
            return numTelTextView;
        }

        CheckBox getCheckBox() {
            return checkBox;
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.contacts_recycler, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int pos) {
        // Récupération du contact
        Contact contact = contactsDataset.get(pos);

        // Mise à jour des informations du contact dans la vue
        if (contact != null && viewHolder != null) {
            // Récupération des éléments de la vue
            TextView nomTextView = viewHolder.getNomTextView();
            TextView numTelTextView = viewHolder.getNumTelTextView();
            CheckBox contactCheckBox = viewHolder.getCheckBox();

            if (nomTextView != null) {
                nomTextView.setText(contact.getName() != null ? contact.getName() : "");
            }

            if (numTelTextView != null) {
                numTelTextView.setText(contact.getPhoneNumber() != null ? contact.getPhoneNumber() : "");
            }

            // Mise à jour de la checkbox
            contactCheckBox.setChecked(contact.isSelected());
            contactCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    contact.setSelected(isChecked);

                    // Si la checkbox est cochée, on ajoute le contact à la liste des contacts sélectionnés
                    if (isChecked) {
                        selectedContacts.add(contact);
                    } else {
                        selectedContacts.remove(contact);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        // Récupération du nombre de contacts
        return contactsDataset.size();
    }

    public ArrayList<Contact> getSelectedContacts() {
        return selectedContacts;
    }

    public void updateContacts(List<Contact> newData) {
        // Mise à jour de la liste des contacts
        contactsDataset.clear();
        contactsDataset.addAll(newData);
        notifyDataSetChanged();
    }
}
