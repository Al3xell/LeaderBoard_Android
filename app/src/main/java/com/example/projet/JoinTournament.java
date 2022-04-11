package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class JoinTournament extends AppCompatActivity {

    Button button_join;
    Button button_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_tournament);

        button_join = findViewById(R.id.button_join_tournament);
        button_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCh();

            }
        });

        button_back = findViewById(R.id.button_retour_tournament);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retourCh();

            }
        });

    }

    private void openCh() {
        Intent intent = new Intent(this, Join_Tournament_team.class);
        startActivity(intent);

    }

    private void retourCh() {
        Intent intent = new Intent(this, EcranPrincipal.class);
        startActivity(intent);

    }
}