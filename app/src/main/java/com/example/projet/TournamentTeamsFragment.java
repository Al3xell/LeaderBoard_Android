package com.example.projet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.projet.adapter.TeamAdapter;
import com.example.projet.adapter.TournamentItemDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TournamentTeamsFragment extends Fragment {

    public TournamentModel tournamentModelTeam;
    public String keyTournamentTeam;

    DatabaseReference tournamentRef = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Tournaments");

    ArrayList<TeamModel> teamsList;
    RecyclerView recyclerViewTeam;
    TeamAdapter teamAdapter;

    public TournamentTeamsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStop() {
        teamsList.clear();
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tournament_teams, container, false);

        FloatingActionButton addButton = view.findViewById(R.id.addTeamButton);

        addButton.setOnClickListener(view1 -> startActivity(new Intent(requireActivity(), CreateTournamentActivity.class)));

        teamsList = new ArrayList<>();

        recyclerViewTeam = view.findViewById(R.id.recyclerTeams);

        tournamentRef.orderByChild("nameTournament").equalTo(tournamentModelTeam.nameTournament).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                teamsList.clear();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    setKeyTeams(ds.getKey());
                    for(DataSnapshot dr : ds.child("Teams").getChildren()) {
                        TeamModel team = dr.getValue(TeamModel.class);
                        teamsList.add(team);
                    }
                }

                teamAdapter = new TeamAdapter(teamsList);

                recyclerViewTeam.setAdapter(teamAdapter);
                recyclerViewTeam.addItemDecoration(new TournamentItemDecoration());
                teamAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.refreshTeams);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            teamsList.clear();
            tournamentRef.child(getKeyTeams()).child("Teams").addValueEventListener(new ValueEventListener() {

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    teamsList.clear();
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        TeamModel team = ds.getValue(TeamModel.class);
                        teamsList.add(team);
                    }

                    teamAdapter = new TeamAdapter(teamsList);

                    recyclerViewTeam.setAdapter(teamAdapter);
                    recyclerViewTeam.addItemDecoration(new TournamentItemDecoration());
                    teamAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        return view;
    }

    public void setTournament(TournamentModel tournament) {
        this.tournamentModelTeam = tournament;
    }
    public void setKeyTeams(String key) {
        this.keyTournamentTeam = key;
    }
    public String getKeyTeams() { return this.keyTournamentTeam; }
}