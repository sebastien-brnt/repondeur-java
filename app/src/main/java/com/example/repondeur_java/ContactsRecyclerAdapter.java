package com.example.repondeur_java;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ContactsRecyclerAdapter extends RecyclerView.Adapter<ContactsRecyclerAdapter.ViewHolder> {
    List<Contact> contactsDataset;

    public ContactsRecyclerAdapter(List<Contact> dataset) {
        contactsDataset = dataset;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nomTextView;
        private final TextView numTelTextView;

        ViewHolder(View v) {
            super(v);
            nomTextView = v.findViewById(R.id.contact_name);
            numTelTextView = v.findViewById(R.id.contact_phone_number);
        }

        TextView getNomTextView() {
            return nomTextView;
        }

        TextView getNumTelTextView() {
            return numTelTextView;
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
            TextView nomTextView = viewHolder.getNomTextView();
            TextView numTelTextView = viewHolder.getNumTelTextView();

            if (nomTextView != null) {
                nomTextView.setText(contact.getName() != null ? contact.getName() : "");
            }

            if (numTelTextView != null) {
                numTelTextView.setText(contact.getPhoneNumber() != null ? contact.getPhoneNumber() : "");
            }
        }
    }

    @Override
    public int getItemCount() {
        // Récupération du nombre de contacts
        return contactsDataset.size();
    }

    public void updateContacts(List<Contact> newData) {
        // Mise à jour de la liste des contacts
        contactsDataset.clear();
        contactsDataset.addAll(newData);
        notifyDataSetChanged();
    }
}
