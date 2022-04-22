package com.example.projet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projet.adapter.TeamAdapter;
import com.example.projet.adapter.TournamentItemDecoration;
import com.example.projet.adapter.TournamentSearch;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class TournamentTeamsFragment extends Fragment {

    private TournamentModel tournamentModel;
    private String keyTournament;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    DatabaseReference tournamentRef = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Tournaments");
    DatabaseReference userRef = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");

    ArrayList<String> teamsList;
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

        tournamentRef.orderByChild("nameTournament").equalTo(tournamentModel.nameTournament).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    if(ds.exists()){
                        setKey(ds.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        tournamentRef.child(keyTournament).child("Teams").addValueEventListener(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                teamsList.clear();
                for(DataSnapshot ds : snapshot.getChildren()) {
                }

                tournamentTeams = new TournamentSearch(teamsList, listener);

                verticalRecyclerView.setAdapter(tournamentTeams);
                verticalRecyclerView.addItemDecoration(new TournamentItemDecoration());
                tournamentTeams.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.refreshTeams);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            teamsList.clear();
            tournamentRef.orderByChild("nameTournamentLower").addListenerForSingleValueEvent(new ValueEventListener() {

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        long numberPlayersTotal = (long)Objects.requireNonNull(ds.child("numberTeams").getValue())*(long)Objects.requireNonNull(ds.child("numberPlayers").getValue());
                        String startDate = ds.child("startDate").getValue(String.class);
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        assert startDate != null;
                        try {
                            if(new Date().compareTo(sdf.parse(startDate)) < 0 && ds.child("players").getChildrenCount() < numberPlayersTotal) {
                                assert user != null;
                                if (!ds.child("players").child(user.getUid()).exists()) {
                                    TournamentModel tournament = ds.getValue(TournamentModel.class);
                                    teamsList.add(tournament);
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    tournamentTeams.teamsList = teamsList;
                    tournamentTeams.notifyDataSetChanged();
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
        this.tournamentModel = tournament;
    }
    public void setKey(String key) {
        this.keyTournament = key;
    }
    public String getKey() {
        return this.keyTournament;
    }
}