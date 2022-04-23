package com.example.projet;

import static android.view.View.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.projet.adapter.PlayerAdapter;
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

public class TeamPlayersActivity extends AppCompatActivity {

    private TeamModel team;
    private TournamentModel tournament;
    private String keyTournament;
    private ArrayList<UserModel> users;
    public UserModel currentUser;

    public MenuItem leaveTeamButton;
    public MenuItem deleteTeamButton;

    RecyclerView recyclerViewPlayers;
    PlayerAdapter playersAdapter;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public PlayerAdapter.RecyclerViewClickListener listener;

    DatabaseReference tournamentRef = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Tournaments");


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_in_team, menu);
        leaveTeamButton = menu.findItem(R.id.leaveTeamButton);
        deleteTeamButton = menu.findItem(R.id.deleteTeamButton);
        if(team.getPlayers()!=null){
            leaveTeamButton.setVisible(team.getPlayers().containsKey(user.getUid()));
            deleteTeamButton.setVisible(team.getAdmin().equals(user.getUid()));
        }
        else {
            leaveTeamButton.setVisible(false);
            deleteTeamButton.setVisible(false);
        }
        return true;
    }

    private void leaveTeam() {
        tournamentRef.child(keyTournament).child("teams").child(team.getId()).child("players").child(user.getUid()).removeValue();
        team.players.remove(user.getUid());
        Intent returnBack = new Intent(this,TournamentSelectActivity.class);
        returnBack.putExtra("Tournament", tournament);
        startActivity(returnBack);
        finish();
    }
    private void deleteTeam() {
        tournamentRef.child(keyTournament).child("teams").child(team.getId()).removeValue();
        if(tournament.getTeams()!=null) {
            tournament.teams.remove(team.getId());
        }
        Intent returnBack = new Intent(this,TournamentSelectActivity.class);
        returnBack.putExtra("Tournament", tournament);
        startActivity(returnBack);
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        team = (TeamModel) getIntent().getSerializableExtra("TeamModel");
        tournament = (TournamentModel) getIntent().getSerializableExtra("TournamentModel");
        keyTournament = getIntent().getStringExtra("key");
        currentUser = (UserModel) getIntent().getSerializableExtra("currentUser");

        setContentView(R.layout.activity_team_players);
        FloatingActionButton floatingActionButtonPlayers = findViewById(R.id.joinTeamButton);

        tournamentRef.child(keyTournament).child("teams").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    if(ds.child("players").child(user.getUid()).exists()){
                        floatingActionButtonPlayers.setVisibility(GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        users = new ArrayList<>();

        recyclerViewPlayers = findViewById(R.id.recyclerPlayers);
        setOnClickListener();
        tournamentRef.child(keyTournament).child("teams").child(team.getId()).child("players").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, UserModel> players = team.getPlayers();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    if(players != null){
                        if (players.containsKey(ds.getKey())) {
                            users.add(ds.getValue(UserModel.class));
                        }
                    }
                }
                playersAdapter = new PlayerAdapter(users, listener);
                recyclerViewPlayers.setAdapter(playersAdapter);
                playersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.refreshPlayers);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            users.clear();
            tournamentRef.child(keyTournament).child("teams").child(team.getId()).child("players").addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    HashMap<String, UserModel> players = team.getPlayers();
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        if(players != null){
                            if (players.containsKey(ds.getKey())) {
                                users.add(ds.getValue(UserModel.class));
                            }
                        }
                    }
                    if(team.getPlayers()!=null){
                        leaveTeamButton.setVisible(team.getPlayers().containsKey(user.getUid()));
                        deleteTeamButton.setVisible(team.getAdmin().equals(user.getUid()));
                    }
                    else {
                        leaveTeamButton.setVisible(false);
                        deleteTeamButton.setVisible(false);
                    }
                    playersAdapter = new PlayerAdapter(users, listener);

                    recyclerViewPlayers.setAdapter(playersAdapter);
                    playersAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        Toolbar toolbar = findViewById(R.id.toolbarPlayers);
        toolbar.setTitle(tournament.nameTournament);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(item -> {
            switch(item.getItemId()){
                case R.id.leaveTeamButton:
                    AlertDialog.Builder builder = new AlertDialog.Builder(TeamPlayersActivity.this);
                    builder.setTitle(getString(R.string.leave_team_title))
                            .setMessage(getString(R.string.leave_team_message));
                    builder.setPositiveButton(getString(R.string.confirm), (dialogInterface, i) -> leaveTeam());
                    builder.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.cancel());
                    builder.create().show();
                    break;
                case R.id.deleteTeamButton:
                    AlertDialog.Builder builderDelete = new AlertDialog.Builder(TeamPlayersActivity.this);
                    builderDelete.setTitle(getString(R.string.delete_team_title))
                            .setMessage(getString(R.string.delete_team_message));
                    builderDelete.setPositiveButton(getString(R.string.confirm), (dialogInterface, i) -> deleteTeam());
                    builderDelete.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.cancel());
                    builderDelete.create().show();
                    break;
            }
            return false;
        });

        floatingActionButtonPlayers.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(TeamPlayersActivity.this);
            builder.setTitle(getString(R.string.join_team_title))
                    .setMessage(getString(R.string.join_team_message));
            builder.setPositiveButton(getString(R.string.confirm), (dialogInterface, i) -> joinTeam());
            builder.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.cancel());
            builder.create().show();
        });
    }

    private void joinTeam() {
        if(team.getPlayers() == null) {
            tournamentRef.child(keyTournament).child("teams").child(team.getId()).child("players").child(currentUser.getId()).setValue(currentUser);
            team.players = new HashMap<>();
            team.players.put(user.getUid(), currentUser);
            tournamentRef.child(keyTournament).child("teams").child(team.getId()).child("admin").setValue(user.getUid());
        }
        else if(team.getPlayers().size() < team.getMaxPlayers()){
            tournamentRef.child(keyTournament).child("teams").child(team.getId()).child("players").child(currentUser.getId()).setValue(currentUser);
        }
        else {
            Toast.makeText(this, getString(R.string.error_team_full), Toast.LENGTH_SHORT).show();
        }
    }

    private void setOnClickListener() { listener = this::viewPlayerInfo; }

    private void viewPlayerInfo(View view, int i) {
        Intent playerIntent = new Intent(this, UserProfilePlayerActivity.class);
        playerIntent.putExtra("player", users.get(i));
        playerIntent.putExtra("TeamModel", team);
        playerIntent.putExtra("TournamentModel", tournament);
        playerIntent.putExtra("key", keyTournament);
        startActivity(playerIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent returnBack = new Intent(this,TournamentSelectActivity.class);
        returnBack.putExtra("Tournament", tournament);
        startActivity(returnBack);
        finish();
    }
}