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

        CheckBox getAutomaticResponseCheckBox() {
            return automaticResponseCheckBox;
        }

        CheckBox getSpamCheckBox() {
            return spamCheckBox;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.response_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Response response = responsesDataset.get(position);
        holder.getResponseTextView().setText(response.getText());
        // Définir l'état des cases à cocher en fonction de l'objet Response
        holder.getAutomaticResponseCheckBox().setChecked(response.isAutomaticResponse());
        holder.getSpamCheckBox().setChecked(response.isSpam());
    }

    @Override
    public int getItemCount() {
        return responsesDataset.size();
    }

    public void updateResponses(List<Response> newData) {
        // Affichage en console des réponses
        for (Response response : newData) {
            System.out.println(response.getText());
        }
        responsesDataset.clear();
        responsesDataset.addAll(newData);
        notifyDataSetChanged();
    }
}
