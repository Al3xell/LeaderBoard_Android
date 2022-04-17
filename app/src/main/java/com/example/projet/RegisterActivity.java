package com.example.projet;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
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

import java.util.Objects;

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

    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");

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
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    boolean isPhoneValid(CharSequence phone) {
        if (phone.length() != 10) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(phone).matches();
        }
    }


    private void setupFloatingLabelError() {

        Objects.requireNonNull(labelFirstName.getEditText()).addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0 ) {
                    labelFirstName.setError(getString(R.string.error_name));
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
        Objects.requireNonNull(labelLastName.getEditText()).addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0 ) {
                    labelLastName.setError(getString(R.string.error_surname));
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
        Objects.requireNonNull(labelEmail.getEditText()).addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0 ) {
                    labelEmail.setError(getString(R.string.error_email_required));
                    labelEmail.setErrorEnabled(true);
                }
                else if (!isEmailValid(emailInput.getText().toString())) {
                    labelEmail.setError(getString(R.string.error_email_invalid));
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
        Objects.requireNonNull(labelPassword.getEditText()).addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0 ) {
                    labelPassword.setError(getString(R.string.error_password_required));
                    labelPassword.setErrorEnabled(true);
                }
                else if (text.length() < 6) {
                    labelPassword.setError(getString(R.string.error_password_invalid));
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
        Objects.requireNonNull(labelConfirmPassword.getEditText()).addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0 ) {
                    labelConfirmPassword.setError(getString(R.string.error_confirm_password_required));
                    labelConfirmPassword.setErrorEnabled(true);
                }
                else if (!passwordInput.getText().toString().equals(confirmInput.getText().toString())){
                    labelConfirmPassword.setError(getString(R.string.error_confirm_password_invalid));
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
        Objects.requireNonNull(labelPhone.getEditText()).addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0 ) {
                    labelPhone.setError(getString(R.string.error_phone));
                    labelPhone.setErrorEnabled(true);
                } else if (!isPhoneValid(phoneInput.getText().toString())) {
                    labelPhone.setError(getString(R.string.error_phone_invalid));
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
                        sendEmailVerification();

                        String key = mAuth.getUid();

                        //send info to database
                        assert key != null;
                        databaseReference.child(key).child("firstName").setValue(firstNameInput.getText().toString());
                        databaseReference.child(key).child("lastName").setValue(lastNameInput.getText().toString());
                        databaseReference.child(key).child("password").setValue(passwordInput.getText().toString());
                        databaseReference.child(key).child("phoneNumber").setValue(phoneInput.getText().toString());

                        //starting activity
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        try {
                            throw Objects.requireNonNull(task.getException());
                        }
                        catch (FirebaseAuthUserCollisionException existingUser) {
                            Log.w(TAG, "createUserWithEmail:failure", existingUser);
                            Toast.makeText(RegisterActivity.this, getString(R.string.error_register_exist),
                                    Toast.LENGTH_SHORT).show();
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
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    // Email sent
                });
        // [END send_email_verification]
    }
}