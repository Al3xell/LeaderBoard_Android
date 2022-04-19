package com.example.projet;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class SearchFragment extends Fragment {

    DatabaseReference tournamentRef = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Tournaments");
    DatabaseReference userRef = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");

    ArrayList<TournamentModel> tournamentList;
    RecyclerView verticalRecyclerView;
    TournamentSearch tournamentSearch;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        tournamentList.clear();
        tournamentRef.addValueEventListener(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    String startDate = ds.child("startDate").getValue(String.class);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    if(!sdf.format(new Date()).equals(startDate)) {
                        TournamentModel tournament = ds.getValue(TournamentModel.class);
                        tournamentList.add(tournament);
                    }
                }

                tournamentSearch.tournamentList = tournamentList;
                tournamentSearch.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);


        verticalRecyclerView = view.findViewById(R.id.verticalRecyclerView);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        FloatingActionButton addButton = view.findViewById(R.id.addButton);

        addButton.setOnClickListener(view1 -> startActivity(new Intent(requireActivity(), CreateTournamentActivity.class)));

        tournamentList = new ArrayList<>();
        tournamentSearch = new TournamentSearch(tournamentList);

        verticalRecyclerView.setAdapter(tournamentSearch);
        verticalRecyclerView.addItemDecoration(new TournamentItemDecoration());

        tournamentRef.addValueEventListener(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    String startDate = ds.child("startDate").getValue(String.class);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                   if(!sdf.format(new Date()).equals(startDate)) {
                        TournamentModel tournament = ds.getValue(TournamentModel.class);
                        tournamentList.add(tournament);
                    }
                }

                tournamentSearch.tournamentList = tournamentList;
                tournamentSearch.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            tournamentList.clear();
            tournamentRef.addListenerForSingleValueEvent(new ValueEventListener() {

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        String startDate = ds.child("startDate").getValue(String.class);
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        if(!sdf.format(new Date()).equals(startDate)) {
                            TournamentModel tournament = ds.getValue(TournamentModel.class);
                            tournamentList.add(tournament);
                        }
                    }

                    tournamentSearch.tournamentList = tournamentList;
                    tournamentSearch.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        return view;
    }
    public void createDialog(TournamentModel tournamentItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        builder.setTitle(getString(R.string.join_tournament_title))
                .setMessage(getString(R.string.join_tournament_message));

        builder.setPositiveButton(getString(R.string.confirm), (dialogInterface, i) -> addUserToTournament(tournamentItem));
        builder.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.cancel());
        AlertDialog joinTournament = builder.create();
        joinTournament.show();
    }

    public void addUserToTournament(TournamentModel tournamentItem) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String nameTournament = tournamentItem.nameTournament;
        tournamentRef.orderByChild("nameTournament").equalTo(nameTournament).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userRef.child(user.getUid()).child("tournamentsIn").child(Objects.requireNonNull(snapshot.getKey())).setValue(tournamentItem);
                tournamentRef.child(snapshot.getKey()).child("players").child(user.getUid()).child("id").setValue(user.getUid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}