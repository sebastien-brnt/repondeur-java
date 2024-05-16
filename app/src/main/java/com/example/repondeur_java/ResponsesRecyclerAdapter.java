package com.example.repondeur_java;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

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

        // Gestion du click sur la case à cocher pour les réponses automatiques
        holder.getAutomaticResponseCheckBox().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean responseCkecked = false;

                // Récupération de l'info si la case est cochée
                boolean checkboxIsChecked = holder.getAutomaticResponseCheckBox().isChecked();

                if (checkboxIsChecked) {
                    // Vérification si une réponse automatique est déjà activée
                    for (Response r : responsesDataset) {
                        if (r.isAutomaticResponse()) {
                            responseCkecked = true;
                            break;
                        }
                    }

                    if (responseCkecked) {
                        // Si une réponse automatique est déjà activée, on désactive celle-ci
                        Toast.makeText(v.getContext(), "Une seule réponse automatique est autorisée", Toast.LENGTH_SHORT).show();
                        response.setAutomaticResponse(false);

                        // On désactive la case à cocher
                        holder.getAutomaticResponseCheckBox().setChecked(false);
                    } else {
                        // Sinon, on active la réponse automatique
                        response.setAutomaticResponse(!response.isAutomaticResponse());
                    }
                } else {
                    // Si la case est décochée, on désactive la réponse automatique
                    response.setAutomaticResponse(false);
                }
            }
        });

        // Gestion du click sur la case à cocher pour les spams
        holder.getSpamCheckBox().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean responseCkecked = false;

                // Récupération de l'info si la case est cochée
                boolean checkboxIsChecked = holder.getSpamCheckBox().isChecked();

                if (checkboxIsChecked) {
                    // Vérification si un spam est déjà activée
                    for (Response r : responsesDataset) {
                        if (r.isSpam()) {
                            responseCkecked = true;
                            break;
                        }
                    }

                    if (responseCkecked) {
                        // Si un spam est déjà activée, on le désactive
                        Toast.makeText(v.getContext(), "Un seul spam est autorisée", Toast.LENGTH_SHORT).show();
                        response.setSpam(false);

                        // On désactive la case à cocher
                        holder.getSpamCheckBox().setChecked(false);
                    } else {
                        // Sinon, on active la réponse automatique
                        response.setSpam(!response.isSpam());
                    }
                } else {
                    // Si la case est décochée, on désactive le spam
                    response.setSpam(false);
                }
            }
        });
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
