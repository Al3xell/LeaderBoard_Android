package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Join_Tournament_team extends AppCompatActivity {

    Button button_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_tournament_team);


        button_back = findViewById(R.id.button_retour_tournament);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retourCh();

            }
        });
    }

    private void retourCh() {
        Intent intent = new Intent(this, JoinTournament.class);
        startActivity(intent);

    }
}