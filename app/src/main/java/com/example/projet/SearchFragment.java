package com.example.projet;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class SearchFragment extends Fragment {

    DatabaseReference tournamentRef = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Tournaments");
    DatabaseReference userRef = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");

    ArrayList<TournamentModel> tournamentList;
    RecyclerView verticalRecyclerView;
    TournamentSearch tournamentSearch;
    TournamentSearch.RecyclerViewClickListener listener;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onStop() {
        super.onStop();
        tournamentList.clear();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.search_top_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.search_hint));
        MenuItemCompat.collapseActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tournamentSearch.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tournamentSearch.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(R.string.search);
        verticalRecyclerView = view.findViewById(R.id.recycler);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        FloatingActionButton addButton = view.findViewById(R.id.addButton);

        addButton.setOnClickListener(view1 -> startActivity(new Intent(requireActivity(), CreateTournamentActivity.class)));

        tournamentList = new ArrayList<>();
        setOnClickListener();
        tournamentList.clear();

        tournamentRef.orderByChild("nameTournamentLower").addValueEventListener(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tournamentList.clear();
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
                                tournamentList.add(tournament);
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                tournamentSearch = new TournamentSearch(tournamentList, listener);

                verticalRecyclerView.setAdapter(tournamentSearch);
                verticalRecyclerView.addItemDecoration(new TournamentItemDecoration());
                tournamentSearch.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            tournamentList.clear();
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
                                    tournamentList.add(tournament);
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
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

    private void setOnClickListener() {
        listener = this::createDialog;
    }

    public void createDialog(View v, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        builder.setTitle(getString(R.string.join_tournament_title))
                .setMessage(getString(R.string.join_tournament_message));

        builder.setPositiveButton(getString(R.string.confirm), (dialogInterface, i) -> addUserToTournament(position));
        builder.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.cancel());
        AlertDialog joinTournament = builder.create();
        joinTournament.show();
    }

    public void addUserToTournament(int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TournamentModel tournament = tournamentList.get(position);
        String nameTournament = tournament.nameTournament;
            tournamentRef.orderByChild("nameTournament").equalTo(nameTournament).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    long numberPlayersTotal = (long)Objects.requireNonNull(ds.child("numberTeams").getValue())*(long)Objects.requireNonNull(ds.child("numberPlayers").getValue());
                    if (Objects.equals(ds.child("nameTournament").getValue(String.class), nameTournament)) {
                        if(ds.child("players").getChildrenCount() < numberPlayersTotal){
                            assert user != null;
                            userRef.child(user.getUid()).child("tournamentsIn").child(Objects.requireNonNull(ds.getKey())).setValue(ds.getValue());
                            ds.getRef().child("players").child(user.getUid()).child("id").setValue(user.getUid());
                        }
                        else {
                            Toast.makeText(requireContext(), getString(R.string.error_tournament_full), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}