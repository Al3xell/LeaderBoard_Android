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

    DatabaseReference tournamentRef = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Tournaments");
    DatabaseReference userRef = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");

    ArrayList<TournamentModel> teamsList;
    RecyclerView verticalRecyclerView;
    TournamentSearch tournamentTeams;
    TournamentSearch.RecyclerViewClickListener listener;

    public TournamentTeamsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStop() {
        super.onStop();
        teamsList.clear();
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

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(R.string.search);
        verticalRecyclerView = view.findViewById(R.id.recyclerTeams);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        FloatingActionButton addButton = view.findViewById(R.id.addButton);

        addButton.setOnClickListener(view1 -> startActivity(new Intent(requireActivity(), CreateTournamentActivity.class)));

        teamsList = new ArrayList<>();


        tournamentRef.orderByChild("nameTournamentLower").addValueEventListener(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                teamsList.clear();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String startDate = ds.child("startDate").getValue(String.class);

                    long numberPlayersTotal = (long)Objects.requireNonNull(ds.child("numberTeams").getValue())*(long)Objects.requireNonNull(ds.child("numberPlayers").getValue());
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        assert startDate != null;
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
}