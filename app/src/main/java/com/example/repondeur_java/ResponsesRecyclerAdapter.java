package com.example.repondeur_java;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ResponsesRecyclerAdapter extends RecyclerView.Adapter<ResponsesRecyclerAdapter.ViewHolder> {
    private List<Response> responsesDataset;

    public ResponsesRecyclerAdapter(List<Response> dataset) {
        responsesDataset = dataset;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView responseTextView;
        private final CheckBox automaticResponseCheckBox;
        private final CheckBox spamCheckBox;

        ViewHolder(View v) {
            super(v);
            responseTextView = v.findViewById(R.id.response_text);
            automaticResponseCheckBox = v.findViewById(R.id.automation_checkbox);
            spamCheckBox = v.findViewById(R.id.spam_checkbox);
        }

        TextView getResponseTextView() {
            return responseTextView;
        }

        TextView getAutomaticResponseCheckBox() {
            return automaticResponseCheckBox;
        }

        CheckBox getSpamCheckBox() {
            return spamCheckBox;
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.response_recycler, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int pos) {
        // Récupération de la réponse
        Response response = responsesDataset.get(pos);

        // Affichage de la réponse
        viewHolder.getResponseTextView().setText(response.getText());
    }

    @Override
    public int getItemCount() {
        // Récupération du nombre de réponses
        return responsesDataset.size();
    }

    public void updateContacts(List<Response> newData) {
        // Mise à jour de la liste des réponses
        responsesDataset.clear();
        responsesDataset.addAll(newData);
        notifyDataSetChanged();
    }
}
