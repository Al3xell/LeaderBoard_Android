package com.example.projet.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet.R;
import com.example.projet.TournamentModel;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TournamentSearch extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    ArrayList<TournamentModel> tournamentList;
    public TextView nameText, startText;


    public TournamentSearch(ArrayList list){

        tournamentList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater
                .from(parent.getContext());
        View view = inflater.inflate(R.layout.vertical_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {;
        //récupe les coordonnées du tournoi
        TournamentModel currentTournament = tournamentList.get(position);


        nameText = holder.itemView.findViewById(R.id.tournamentNameSearch);
        startText = holder.itemView.findViewById(R.id.tournamentStartSearch);

        nameText.setText(currentTournament.nameTournament) ;
        startText.setText(currentTournament.startDate);
    }

    @Override
    public int getItemCount() {
        return tournamentList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        public ImageView imageTournament;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageTournament = itemView.findViewById(R.id.imageSearch);
        }
    }
}
