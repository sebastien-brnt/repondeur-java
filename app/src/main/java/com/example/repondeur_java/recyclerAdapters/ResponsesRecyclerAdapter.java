package com.example.repondeur_java.recyclerAdapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.repondeur_java.R;
import com.example.repondeur_java.entities.Response;
import com.example.repondeur_java.utils.ResponsesManager;

import java.util.ArrayList;
import java.util.List;

public class ResponsesRecyclerAdapter extends RecyclerView.Adapter<ResponsesRecyclerAdapter.ViewHolder> {
    private List<Response> responsesDataset;
    private Context context;

    public ResponsesRecyclerAdapter(Context context, List<Response> dataset) {
        this.context = context;
        responsesDataset = dataset;
    }

    /**************************
     * Gestion ViewHolder
    **************************/
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView responseTextView;
        private final CheckBox automaticResponseCheckBox;
        private final CheckBox spamCheckBox;
        private final ImageView deleteButton;

        ViewHolder(View v) {
            super(v);
            responseTextView = v.findViewById(R.id.response_text);
            automaticResponseCheckBox = v.findViewById(R.id.automation_checkbox);
            deleteButton = v.findViewById(R.id.response_delete);
            spamCheckBox = v.findViewById(R.id.spam_checkbox);
        }

        TextView getResponseTextView() {
            return responseTextView;
        }

        CheckBox getAutomaticResponseCheckBox() {
            return automaticResponseCheckBox;
        }
        ImageView getDeleteButton() {
            return deleteButton;
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
                boolean responseChecked = false;

                // Récupération de l'info si la case est cochée
                boolean checkboxIsChecked = holder.getAutomaticResponseCheckBox().isChecked();

                if (checkboxIsChecked) {
                    // Vérification si une réponse automatique est déjà activée
                    for (Response r : responsesDataset) {
                        if (r.isAutomaticResponse()) {
                            responseChecked = true;
                            break;
                        }
                    }

                    if (responseChecked) {
                        // Si une réponse automatique est déjà activée, on désactive celle-ci
                        Toast.makeText(v.getContext(), "Une seule réponse automatique est autorisée", Toast.LENGTH_SHORT).show();
                        response.setAutomaticResponse(false);

                        // On désactive la case à cocher
                        holder.getAutomaticResponseCheckBox().setChecked(false);
                    } else {
                        // Sinon, on active la réponse automatique
                        response.setAutomaticResponse(true);

                        // Enregistrement de la réponse dans la mémoire du téléphone
                        saveAutoResponseMessage(response.getText());
                    }
                } else {
                    // Si la case est décochée, on désactive la réponse automatique
                    response.setAutomaticResponse(false);
                }

                // Sauvegarder les réponses après la modification
                ResponsesManager.setResponses(context, new ArrayList<>(responsesDataset));
            }
        });

        // Gestion du click sur la case à cocher pour les spams
        holder.getSpamCheckBox().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean responseChecked = false;

                // Récupération de l'info si la case est cochée
                boolean checkboxIsChecked = holder.getSpamCheckBox().isChecked();

                if (checkboxIsChecked) {
                    // Vérification si un spam est déjà activé
                    for (Response r : responsesDataset) {
                        if (r.isSpam()) {
                            responseChecked = true;
                            break;
                        }
                    }

                    if (responseChecked) {
                        // Si un spam est déjà activé, on le désactive
                        Toast.makeText(v.getContext(), "Un seul spam est autorisé", Toast.LENGTH_SHORT).show();
                        response.setSpam(false);

                        // On désactive la case à cocher
                        holder.getSpamCheckBox().setChecked(false);
                    } else {
                        // Sinon, on active le spam
                        response.setSpam(true);
                    }
                } else {
                    // Si la case est décochée, on désactive le spam
                    response.setSpam(false);
                }

                // Sauvegarder les réponses après la modification
                ResponsesManager.setResponses(context, new ArrayList<>(responsesDataset));
            }
        });


        // Gestion du click sur le bouton de suppression
        holder.getDeleteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responsesDataset.remove(response);
                ResponsesManager.setResponses(context, new ArrayList<>(responsesDataset));
                notifyDataSetChanged();
            }
        });
    }

    /**********************************
     * Mise à jour liste des réponses
     **********************************/
    public void updateResponses(List<Response> newData) {
        // Affichage en console des réponses
        for (Response response : newData) {
            System.out.println(response.getText());
        }
        responsesDataset.clear();
        responsesDataset.addAll(newData);
        notifyDataSetChanged();
    }

    /**************************
     * Récup nombre de réponses
    **************************/
    @Override
    public int getItemCount() {
        return responsesDataset.size();
    }


    /************************************
     * Enregistrement réponse automatique
    ************************************/
    // Méthode pour enregistrer le message de réponse automatique dans les SharedPreferences
    private void saveAutoResponseMessage(String message) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AutoResponsePrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("automaticResponseMessage", message);
        editor.apply();
    }

}
