package com.example.projet.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet.R;
import com.example.projet.TeamModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.ViewHolder> {

    public ArrayList<TeamModel> teamsList;

    public TeamAdapter(ArrayList<TeamModel> teams){
        this.teamsList = teams;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.team_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.display(teamsList.get(position));
    }

    @Override
    public int getItemCount() {
        return teamsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView teamImage;
        private TextView  nameTeam;
        private TextView  numberTeam;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            teamImage  = itemView.findViewById(R.id.imageTeam);
            nameTeam   = itemView.findViewById(R.id.nameTeamTeam);
            numberTeam = itemView.findViewById(R.id.numberTeamTeam);

        }
        public void display(TeamModel team) {
            nameTeam.setText(team.getNameTeam());
            String number = team.getPlayers().size()+"/"+team.getMaxPlayers();
            numberTeam.setText(number);
            if(team.getImageURI().equals("default")) {
                teamImage.setImageResource(R.drawable.tournament);
            }
            else {
                Picasso.get().load(team.getImageURI()).into(teamImage);
            }
        }
    }
}
