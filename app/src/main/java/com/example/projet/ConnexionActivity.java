package com.example.projet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.Objects;

import javax.annotation.Nullable;

public class ConnexionActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");

    private static final String TAG = "GOOGLE_SIGN_IN_TAG";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        createRequest();

        firebaseAuth = FirebaseAuth.getInstance();

        Button btn_google = findViewById(R.id.signInGoogle);
        Button btn_email  = findViewById(R.id.signInEmail);

        btn_google.setOnClickListener(v -> {
            Log.d(TAG, "onClick : begin Google SignIn");
            signInGoogle();
        });

        btn_email.setOnClickListener(v -> {
            Log.d(TAG, "onClick : begin Google SignIn");
            startActivity(new Intent(ConnexionActivity.this, LoginActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.getIdToken(true);
            startActivity(new Intent(ConnexionActivity.this, MainActivity.class));
            finish();
        }
        else {
            googleSignInClient.signOut();
        }
    }

    private void createRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this,gso);
    }

    private void signInGoogle() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    Log.d(TAG, "onSuccess: Logged In");

                    //get logged in user
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    //get user info
                    assert firebaseUser != null;
                    String uid = firebaseUser.getUid();
                    String email = firebaseUser.getEmail();

                    Log.d(TAG, "onSuccess: Email :"+email);
                    Log.d(TAG, "onSuccess: UID :"+uid);

                    //check if user is new or existing
                    if (Objects.requireNonNull(authResult.getAdditionalUserInfo()).isNewUser()) {
                        Log.d(TAG, "onSuccess: AccountFragment Created...\n"+email);
                        Toast.makeText(ConnexionActivity.this, getString(R.string.account_create)+email, Toast.LENGTH_SHORT).show();
                        sendEmailVerification();
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds : snapshot.getChildren()) {
                                    if(Objects.equals(firebaseUser.getEmail(), ds.child("email").getValue()))
                                    {
                                        ds.getRef().removeValue();
                                    }
                                }
                                String[] displayName = Objects.requireNonNull(firebaseUser.getDisplayName()).split(" ");
                                UserModel userModel = new UserModel(firebaseUser.getUid(), email, displayName[0], displayName[1], "", "", Objects.requireNonNull(firebaseUser.getPhotoUrl()).toString());
                                databaseReference.child(firebaseUser.getUid()).setValue(userModel);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else {
                        Log.d(TAG, "onSuccess: Existing user...\n"+email);
                    }

                    //start profile activity
                    startActivity(new Intent(ConnexionActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: Logging failed"+e.getMessage()));
    }
    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    // Email sent
                });
        // [END send_email_verification]
    }
}
