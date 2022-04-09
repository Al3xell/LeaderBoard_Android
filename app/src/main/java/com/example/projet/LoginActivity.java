package com.example.projet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        Button btnLogin = findViewById(R.id.loginButton);
        Button btnRegister = findViewById(R.id.registerButton);
        ImageButton btnReturn = findViewById(R.id.returnButton);
        EditText emailInput = findViewById(R.id.txtEmail);
        EditText passwordInput = findViewById(R.id.txtPassword);

        btnReturn.setOnClickListener(back -> {
            startActivity(new Intent(LoginActivity.this, FirebaseUI.class));
            finish();
        });

        btnLogin.setOnClickListener(login -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            if (!(email.equals("") && password.equals(""))) {
                signIn(email, password);
            } else {
                Toast.makeText(LoginActivity.this, "Enter valid information !",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnRegister.setOnClickListener(register -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            intent.putExtra("Email", email);
            intent.putExtra("Password", password);
            mAuth.signOut();
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Wrong Email or Password !",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        // [END sign_in_with_email]
    }
}