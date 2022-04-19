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

public class TournamentSearch extends RecyclerView.Adapter<TournamentSearch.ViewHolder> {


    public ArrayList<TournamentModel> tournamentList;


    public TournamentSearch(ArrayList<TournamentModel> listTournament){
        tournamentList = listTournament;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.vertical_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.display(tournamentList.get(position));
    }


    @Override
    public int getItemCount() {
        return tournamentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageTournament;
        TextView nameText, startText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageTournament = itemView.findViewById(R.id.imageSearch);
            nameText = itemView.findViewById(R.id.tournamentNameSearch);
            startText = itemView.findViewById(R.id.tournamentStartSearch);
        }

        public void display(TournamentModel tournament) {
            nameText.setText(tournament.nameTournament);
            startText.setText(tournament.startDate);
        }

    }
}
