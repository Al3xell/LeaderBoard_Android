package com.example.projet;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.projet.adapter.TeamAdapter;
import com.example.projet.adapter.TournamentItemDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TournamentTeamsFragment extends Fragment {

    TournamentModel tournamentModelTeam;
    UserModel currentUserModel;
    public String keyTournamentTeam;

    public FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public TeamAdapter.RecyclerViewClickListener listener;

    DatabaseReference tournamentRef = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Tournaments");

    DatabaseReference usersRef = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");

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
        usersRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUserModel = snapshot.getValue(UserModel.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FloatingActionButton addButton = view.findViewById(R.id.addTeamButton);

        addButton.setOnClickListener(view1 -> addTeam());
        setOnClickListener();
        teamsList = new ArrayList<>();
        recyclerViewTeam = view.findViewById(R.id.recycler);
        tournamentRef.orderByChild("nameTournament").equalTo(tournamentModelTeam.nameTournament).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                teamsList.clear();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    setKeyTeams(ds.getKey());
                    for(DataSnapshot dr : ds.child("Teams").getChildren()) {
                        TeamModel team = dr.getValue(TeamModel.class);
                        assert team != null;
                        if(team.players.containsKey(user.getUid())){
                            addButton.setVisibility(View.GONE);
                        }
                        teamsList.add(team);
                    }
                }

                teamAdapter = new TeamAdapter(teamsList, listener);
                recyclerViewTeam.setAdapter(teamAdapter);
                teamAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            teamsList.clear();
            tournamentRef.child(getKeyTeams()).child("Teams").addValueEventListener(new ValueEventListener() {

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    teamsList.clear();
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        TeamModel team = ds.getValue(TeamModel.class);
                        assert team != null;
                        if(team.players.containsKey(user.getUid())){
                            addButton.setVisibility(View.GONE);
                        }
                        teamsList.add(team);
                    }

                    teamAdapter = new TeamAdapter(teamsList, listener);

                    recyclerViewTeam.setAdapter(teamAdapter);
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

    private void setOnClickListener() { listener = this::viewTeam; }

    private void viewTeam(View view, int i) {
        Intent teamIntent = new Intent(requireActivity(), TeamPlayersActivity.class);
        teamIntent.putExtra("TeamModel", teamsList.get(i));
        teamIntent.putExtra("TournamentModel", tournamentModelTeam);
        teamIntent.putExtra("key", getKeyTeams());
        startActivity(teamIntent);
        requireActivity().finish();
    }

    private void addTeam() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(getString(R.string.create_team))
                .setMessage(getString(R.string.create_team_message));
        final EditText nameInputDialog = new EditText(getContext());
        nameInputDialog.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(nameInputDialog);

        builder.setPositiveButton(getString(R.string.confirm), (dialogInterface, i) -> {
            HashMap<String, UserModel> players = new HashMap<>();
            players.put(user.getUid(), currentUserModel);
            tournamentRef.child(getKeyTeams()).child("Teams").push().setValue(new TeamModel(tournamentModelTeam.numberPlayers, nameInputDialog.getText().toString(), players, "default"));
        });
        builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel());
        builder.create().show();
    }

    public void setTournament(TournamentModel tournament) {
        this.tournamentModelTeam = tournament;
    }
    public void setKeyTeams(String key) {
        this.keyTournamentTeam = key;
    }
    public String getKeyTeams() { return this.keyTournamentTeam; }
}