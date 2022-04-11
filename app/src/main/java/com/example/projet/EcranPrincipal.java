package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EcranPrincipal extends AppCompatActivity {

    Button button_join;
    Button button_create;
    Button button_urt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecran_principal);

        button_join = findViewById(R.id.button_join);
        button_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCh();

            }
        });

        button_create = findViewById(R.id.button_add);
        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCh_1();

            }
        });

        button_urt = findViewById(R.id.button_urt);
        button_urt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCh_2();

            }
        });
    }
    private void openCh() {
        Intent intent = new Intent(this, JoinTournament.class);
        startActivity(intent);

    }

    private void openCh_1() {
        Intent intent = new Intent(this, CreateTournament.class);
        startActivity(intent);

    }

    private void openCh_2() {
        Intent intent = new Intent(this, YourTournament.class);
        startActivity(intent);

    }
}