package com.example.projet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.projet.adapter.TournamentItemDecoration;
import com.example.projet.adapter.TournamentSearch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment {

    DatabaseReference tournamentRef = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Tournaments");
    DatabaseReference userRef = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    ArrayList<TournamentModel> tournamentList;
    RecyclerView recyclerView;
    TournamentSearch tournamentAdapter;
    TournamentSearch.RecyclerViewClickListener listener;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        tournamentList.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(R.string.home);


        recyclerView = view.findViewById(R.id.verticalRecyclerViewHome);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        tournamentList = new ArrayList<>();
        setOnClickListener();

        userRef.child(user.getUid()).child("tournamentsIn").orderByChild("nameTournamentLower").addValueEventListener(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tournamentList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {

                    TournamentModel tournament = ds.getValue(TournamentModel.class);
                    tournamentList.add(tournament);

                }

                tournamentAdapter = new TournamentSearch(tournamentList, listener);

                recyclerView.setAdapter(tournamentAdapter);
                recyclerView.addItemDecoration(new TournamentItemDecoration());
                tournamentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutHome);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            tournamentList.clear();
            userRef.child(user.getUid()).child("tournamentsIn").orderByChild("nameTournamentLower").addListenerForSingleValueEvent(new ValueEventListener() {

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    tournamentList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        TournamentModel tournament = ds.getValue(TournamentModel.class);
                        tournamentList.add(tournament);

                    }

                    tournamentAdapter.tournamentList = tournamentList;
                    tournamentAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
        return view;
    }

    private void setOnClickListener() {
        listener = this::selectTournament;
    }

    public void selectTournament(View v, int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TournamentModel tournament = tournamentList.get(position);
        Intent tournamentIntent = new Intent(requireActivity(), TournamentSelectActivity.class);
        tournamentIntent.putExtra("Tournament", tournament);
        startActivity(tournamentIntent);
        requireActivity().finish();
    }
}