package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class YourTournament extends AppCompatActivity {

    Button button_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_tournament);

        button_back = findViewById(R.id.button_retour_tournament);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCh();

            }
        });
    }



    private void openCh() {
        Intent intent = new Intent(this, EcranPrincipal.class);
        startActivity(intent);
    }
}