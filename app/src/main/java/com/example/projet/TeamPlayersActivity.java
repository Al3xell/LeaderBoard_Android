package com.example.projet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.projet.adapter.PlayerAdapter;
import com.example.projet.adapter.TeamAdapter;
import com.example.projet.adapter.TournamentItemDecoration;
import com.firebase.ui.auth.data.model.User;
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

public class TeamPlayersActivity extends AppCompatActivity {

    private TeamModel team;
    private TournamentModel tournament;
    private String key;
    private ArrayList<UserModel> users;
    public UserModel currentUser;

    RecyclerView recyclerViewPlayers;
    PlayerAdapter playersAdapter;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public PlayerAdapter.RecyclerViewClickListener listener;

    DatabaseReference usersRef = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");
    DatabaseReference tournamentRef = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Tournaments");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_players);

        team = (TeamModel) getIntent().getSerializableExtra("TeamModel");
        tournament = (TournamentModel) getIntent().getSerializableExtra("TournamentModel");
        key = getIntent().getStringExtra("key");

        users = new ArrayList<>();

        recyclerViewPlayers = findViewById(R.id.recyclerPlayers);
        setOnClickListener();
        usersRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, UserModel> players = team.getPlayers();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    if(players.containsKey(ds.getKey())){
                        users.add(ds.getValue(UserModel.class));
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
            usersRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    HashMap<String, UserModel> players = team.getPlayers();
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        if(players.containsKey(ds.getKey())){
                            users.add(ds.getValue(UserModel.class));
                        }
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

        FloatingActionButton floatingActionButtonPlayers = findViewById(R.id.joinTeamButton);
        usersRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(UserModel.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
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
        if(team.getPlayers().size() < team.getMaxPlayers()){
            tournamentRef.child(key).child("Teams").child("players").child(currentUser.getId()).setValue(currentUser);
            usersRef.child(currentUser.getId()).child("inTeam").setValue(true);
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
        playerIntent.putExtra("key", key);
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