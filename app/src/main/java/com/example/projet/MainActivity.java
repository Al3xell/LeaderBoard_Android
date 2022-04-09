package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private TextView textEmail;
    private Button btn_disconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));


        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        btn_disconnect = findViewById(R.id.btnDisconnect);
        btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();
            }
        });


    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(MainActivity.this, FirebaseUI.class));
            finish();
        }
        else {
            String email = firebaseUser.getEmail();
            textEmail = findViewById(R.id.txtEmail);
            textEmail.setText(email);
        }
    }
}