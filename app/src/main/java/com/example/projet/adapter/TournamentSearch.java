package com.example.projet.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet.R;
import com.example.projet.SearchFragment;

public class TournamentSearch extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    String data1, data2;
    ImageView image;

    public TournamentSearch(String s1, String s2){

        data1 = s1;
        data2 = s2;

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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameText, startText;
        ImageView imageTournament;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.tournamentNameSearch);
            startText = itemView.findViewById(R.id.tournamentStartSearch);
            imageTournament = itemView.findViewById(R.id.imageSearch);
        }
    }
}
