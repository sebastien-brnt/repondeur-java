package com.example.repondeur_java.recyclerAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.repondeur_java.entities.Contact;
import com.example.repondeur_java.R;

import java.util.List;

public class ContactsRecyclerAdapter extends RecyclerView.Adapter<ContactsRecyclerAdapter.ViewHolder> {
    private final List<Contact> contactsDataset;
    private final List<Contact> selectedContacts;
    private boolean showCheckBox;

    public ContactsRecyclerAdapter(List<Contact> dataset, List<Contact> selectedContacts, boolean showCheckBox) {
        this.contactsDataset = dataset;
        this.selectedContacts = selectedContacts;
        this.showCheckBox = showCheckBox;
    }

    /**************************
     * Gestion ViewHolder
    **************************/
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

        // Si l'option showCheckBox est désactivée, on cache la case à cocher
        if (!showCheckBox) {
            CheckBox checkBox = view.findViewById(R.id.contact_checkbox);
            checkBox.setVisibility(View.GONE);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int pos) {
        Contact contact = contactsDataset.get(pos);
        if (contact != null && viewHolder != null) {
            TextView nomTextView = viewHolder.getNomTextView();
            TextView numTelTextView = viewHolder.getNumTelTextView();
            CheckBox contactCheckBox = viewHolder.getCheckBox();

            if (nomTextView != null) {
                nomTextView.setText(contact.getName() != null ? contact.getName() : "");
            }

            if (numTelTextView != null) {
                numTelTextView.setText(contact.getPhoneNumber() != null ? contact.getPhoneNumber() : "");
            }

            // Vérifier si le contact est dans la liste des contacts sélectionnés
            if (selectedContacts.contains(contact)) {
                // Si le contact est sélectionné, cocher la case
                contactCheckBox.setChecked(true);
            } else {
                // Sinon, décocher la case
                contactCheckBox.setChecked(false);
            }

            contactCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                contact.setSelected(isChecked);
                if (isChecked) {
                    selectedContacts.add(contact);
                } else {
                    selectedContacts.remove(contact);
                }
            });
        }
    }

    /*********************************
     * Mise à jour liste des contacts
    *********************************/
    // Méthode pour mettre à jour la liste des contacts
    public void updateContacts(List<Contact> newData) {
        contactsDataset.clear();
        contactsDataset.addAll(newData);
        notifyDataSetChanged();
    }

    /**************************
     * Récup nombre de contacts
    **************************/
    @Override
    public int getItemCount() {
        return contactsDataset.size();
    }


}
