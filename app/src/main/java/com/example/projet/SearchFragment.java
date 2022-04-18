package com.example.projet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projet.adapter.TournamentItemDecoration;
import com.example.projet.adapter.TournamentSearch;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Tournaments");

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
        // fetch updated data
        tournamentList = new ArrayList<>();
        tournamentSearch = new TournamentSearch(tournamentList);
        updateData(tournamentList, tournamentSearch);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);


        verticalRecyclerView = view.findViewById(R.id.verticalRecyclerView);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton addButton = view.findViewById(R.id.addButton);

        addButton.setOnClickListener(view1 -> {
           startActivity(new Intent(requireActivity(), CreateTournamentActivity.class));
        });

        tournamentList = new ArrayList<>();
        tournamentSearch = new TournamentSearch(tournamentList);
        updateData(tournamentList, tournamentSearch);

        verticalRecyclerView.setAdapter(tournamentSearch);
        verticalRecyclerView.addItemDecoration(new TournamentItemDecoration());

        return view;
    }

    public void updateData(ArrayList<TournamentModel> tournamentList, TournamentSearch tournamentSearch){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener(){

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){


                    TournamentModel tournament = ds.getValue(TournamentModel.class);
                    tournamentList.add(tournament);
                }
                tournamentSearch.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}