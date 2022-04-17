package com.example.projet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet.adapter.TournamentItemDecoration;
import com.example.projet.adapter.TournamentSearch;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SearchFragment extends Fragment {

    String s1 = "nom du tournoi";
    String s2 = "debut du tournoi";

    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("tournaments");


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        FloatingActionButton addButton = view.findViewById(R.id.addButton);

        addButton.setOnClickListener(view1 -> startActivity(new Intent(requireContext(), CreateTournamentActivity.class)));

        RecyclerView verticalRecyclerView = view.findViewById(R.id.verticalRecyclerView);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TournamentSearch tournamentSearch = new TournamentSearch(s1, s2);
        verticalRecyclerView.setAdapter(tournamentSearch);
        verticalRecyclerView.addItemDecoration(new TournamentItemDecoration());

        return view;
    }
}