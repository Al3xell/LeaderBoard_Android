package com.example.projet;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";

    EditText firstNameInput;
    EditText lastNameInput;
    EditText emailInput;
    EditText passwordInput;
    EditText confirmInput;
    EditText phoneInput;

    TextInputLayout labelFirstName;
    TextInputLayout labelLastName;
    TextInputLayout labelEmail;
    TextInputLayout labelPassword;
    TextInputLayout labelConfirmPassword;
    TextInputLayout labelPhone;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ImageButton btnReturn = findViewById(R.id.returnButtonRegister);
        Button btnSend = findViewById(R.id.sendInfoButton);

        firstNameInput = findViewById(R.id.txtFirstName);
        lastNameInput = findViewById(R.id.txtLastName);
        emailInput = findViewById(R.id.txtEmailRegister);
        passwordInput = findViewById(R.id.txtPasswordRegister);
        confirmInput = findViewById(R.id.txtConfirmPassword);
        phoneInput = findViewById(R.id.txtPhone);

        labelFirstName = findViewById(R.id.inputFirstName);
        labelLastName = findViewById(R.id.inputLastName);
        labelEmail = findViewById(R.id.inputEmailRegister);
        labelPassword = findViewById(R.id.inputPassword);
        labelConfirmPassword = findViewById(R.id.inputConfirmPasword);
        labelPhone = findViewById(R.id.inputPhone);
        mAuth = FirebaseAuth.getInstance();

        emailInput.setText(getIntent().getStringExtra("Email"));
        passwordInput.setText(getIntent().getStringExtra("Password"));

        btnReturn.setOnClickListener(back -> {
            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            finish();
        });

        btnSend.setOnClickListener(send -> {
            if(!labelEmail.isErrorEnabled() && !emailInput.getText().toString().equals("")
            && !labelConfirmPassword.isErrorEnabled() && !confirmInput.getText().toString().equals("")
            && !labelFirstName.isErrorEnabled() && !firstNameInput.getText().toString().equals("")
            && !labelLastName.isErrorEnabled() && !lastNameInput.getText().toString().equals("")
            && !labelPassword.isErrorEnabled() && !passwordInput.getText().toString().equals("")
            && !labelPhone.isErrorEnabled() && !phoneInput.getText().toString().equals(""))
            {
                createAccount(emailInput.getText().toString(), passwordInput.getText().toString());
            }
        });
        setupFloatingLabelError();
    }
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private void setupFloatingLabelError() {

        labelFirstName.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0 ) {
                    labelFirstName.setError("Prénom requis");
                    labelFirstName.setErrorEnabled(true);
                } else {
                    labelFirstName.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        labelLastName.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0 ) {
                    labelLastName.setError("Nom requis");
                    labelLastName.setErrorEnabled(true);
                } else {
                    labelLastName.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        labelEmail.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0 ) {
                    labelFirstName.setError("Mot de passe requis");
                    labelFirstName.setErrorEnabled(true);
                }
                else if (!isEmailValid(emailInput.getText().toString())) {
                    labelEmail.setError("Email Invalid");
                    labelEmail.setErrorEnabled(true);
                } else {
                    labelEmail.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        labelPassword.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0 ) {
                    labelPassword.setError("Mot de passe requis");
                    labelPassword.setErrorEnabled(true);
                }
                else if (text.length() < 6) {
                    labelPassword.setError("Le mot de passe doit être de 6 caractères minimums");
                    labelPassword.setErrorEnabled(true);
                }

                else {
                    labelPassword.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        labelConfirmPassword.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0 ) {
                    labelConfirmPassword.setError("Confirmation requise");
                    labelConfirmPassword.setErrorEnabled(true);
                }
                else if (!passwordInput.getText().toString().equals(confirmInput.getText().toString())){
                    labelConfirmPassword.setError("Les mots de passes ne correspondent pas");
                    labelConfirmPassword.setErrorEnabled(true);
                }

                else {
                    labelConfirmPassword.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        labelPhone.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0 ) {
                    labelPhone.setError("Numéro de téléphone requis requis");
                    labelPhone.setErrorEnabled(true);
                } else {
                    labelPhone.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        sendEmailVerification();

                        String key = databaseReference.child("users").push().getKey();

                        //send info to database
                        databaseReference.child("users").child(key).child("firstName").setValue(firstNameInput.getText().toString());
                        databaseReference.child("users").child(key).child("lastName").setValue(lastNameInput.getText().toString());
                        databaseReference.child("users").child(key).child("email").setValue(emailInput.getText().toString());
                        databaseReference.child("users").child(key).child("password").setValue(passwordInput.getText().toString());
                        databaseReference.child("users").child(key).child("phoneNumber").setValue(phoneInput.getText().toString());

                        //starting activity
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        try {
                            throw task.getException();
                        }
                        catch (FirebaseAuthUserCollisionException existingUser) {
                            Log.w(TAG, "createUserWithEmail:failure", existingUser);
                            Toast.makeText(RegisterActivity.this, "User Already Exist !\nUpdating Info",
                                    Toast.LENGTH_SHORT).show();
                            databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot ds : snapshot.getChildren()) {
                                        Log.d(TAG, ds.child("email").getValue().toString());
                                        if(ds.child("email").getValue().toString().equals(email))
                                        {
                                            databaseReference.child("users").child(ds.getKey()).child("firstName").setValue(firstNameInput.getText().toString());
                                            databaseReference.child("users").child(ds.getKey()).child("lastName").setValue(lastNameInput.getText().toString());
                                            databaseReference.child("users").child(ds.getKey()).child("email").setValue(emailInput.getText().toString());
                                            databaseReference.child("users").child(ds.getKey()).child("password").setValue(passwordInput.getText().toString());
                                            databaseReference.child("users").child(ds.getKey()).child("phoneNumber").setValue(phoneInput.getText().toString());
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }
                        catch (Exception e)
                        {
                            Log.d(TAG, "onComplete: " + e.getMessage());
                        }

                    }
                });
        // [END create_user_with_email]
    }
    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    // Email sent
                });
        // [END send_email_verification]
    }
}