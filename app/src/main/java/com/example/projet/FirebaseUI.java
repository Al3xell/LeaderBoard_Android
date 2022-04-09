package com.example.projet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.annotation.Nullable;

public class FirebaseUI extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;

    private FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference();


    private static final String TAG = "GOOGLE_SIGN_IN_TAG";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ui);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        Button btn_google = findViewById(R.id.signInGoogle);
        Button btn_email  = findViewById(R.id.signInEmail);

        btn_google.setOnClickListener(v -> {
            Log.d(TAG, "onClick : begin Google SignIn");
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, RC_SIGN_IN);
        });

        btn_email.setOnClickListener(v -> {
            Log.d(TAG, "onClick : begin Google SignIn");
            startActivity(new Intent(FirebaseUI.this, LoginActivity.class));
            finish();
        });
    }

    private void checkUser() {
        //if user is already signed in then go to profile activity
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            startActivity(new Intent(FirebaseUI.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult : Google SignIn intent result");
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogleAccount(account);
            }
            catch (Exception e) {
                Log.d(TAG, "onActivityResult :"+e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: begin firebase auth with google account");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    Log.d(TAG, "onSuccess: Logged In");

                    //get logged in user
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    //get user info
                    String uid = firebaseUser.getUid();
                    String email = firebaseUser.getEmail();

                    Log.d(TAG, "onSuccess: Email :"+email);
                    Log.d(TAG, "onSuccess: UID :"+uid);

                    //check if user is new or existing
                    if (authResult.getAdditionalUserInfo().isNewUser()) {
                        Log.d(TAG, "onSuccess: Account Created...\n"+email);
                        Toast.makeText(FirebaseUI.this, "Account Created...\n"+email, Toast.LENGTH_SHORT).show();
                        DatabaseReference users = databaseReference.child("users");
                        String key = users.push().getKey();

                        users.child(key).child("email").setValue(email);

                    }
                    else {
                        Log.d(TAG, "onSuccess: Existing user...\n"+email);
                        Toast.makeText(FirebaseUI.this, "Existing user...\n"+email, Toast.LENGTH_SHORT).show();
                    }

                    //start profile activity
                    startActivity(new Intent(FirebaseUI.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: Logging failed"+e.getMessage()));
    }



}