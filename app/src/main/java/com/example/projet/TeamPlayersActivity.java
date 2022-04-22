package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class TeamPlayersActivity extends AppCompatActivity {

    private TeamModel team;
    private TournamentModel tournament;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_players);

        team = (TeamModel) getIntent().getSerializableExtra("TeamModel");
        tournament = (TournamentModel) getIntent().getSerializableExtra("TournamentModel");
        key = getIntent().getStringExtra("key");

        Toolbar toolbar = findViewById(R.id.toolbarPlayers);
        toolbar.setTitle(tournament.nameTournament);
        setSupportActionBar(toolbar);


    }
}