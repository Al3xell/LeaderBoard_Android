package com.example.projet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class AccountFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private Button disconnectButton;
    private Button sendInfoButton;
    private Button sendPassword;
    private Button deleteButton;

    private ImageView avatarImage;

    private TextView displayName;

    private TextInputLayout nameInput;
    private TextInputLayout surnameInput;
    private TextInputLayout passwordInput;
    private TextInputLayout confirmInput;
    private TextInputLayout phoneInput;

    private EditText nameTxt;
    private EditText surnameTxt;
    private EditText passwordTxt;
    private EditText confirmPasswordTxt;
    private EditText phoneTxt;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();

        user = firebaseAuth.getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        initComponents(rootView);
        displayInfo();
        disconnectButton.setOnClickListener(v -> {
            firebaseAuth.signOut();
            checkUser();
        });

        avatarImage.setOnClickListener(v -> startCropActivity());

        sendInfoButton.setOnClickListener(v -> {
            Query checkUser = databaseReference.child(user.getUid());
            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String firstName = snapshot.child("firstName").getValue(String.class);
                        String lastName = snapshot.child("lastName").getValue(String.class);
                        String phone = snapshot.child("phoneNumber").getValue(String.class);
                        if ((!Objects.equals(firstName, nameTxt.getText().toString()) && !nameInput.isErrorEnabled())
                                || (!Objects.equals(lastName, surnameTxt.getText().toString()) && !surnameInput.isErrorEnabled())
                                || (!Objects.equals(phone, phoneTxt.getText().toString()) && !phoneInput.isErrorEnabled())) {

                            databaseReference.child(user.getUid()).child("firstName").setValue(nameTxt.getText().toString());
                            databaseReference.child(user.getUid()).child("lastName").setValue(surnameTxt.getText().toString());
                            databaseReference.child(user.getUid()).child("phoneNumber").setValue(phoneTxt.getText().toString());

                            Toast.makeText(requireContext(), R.string.update_info, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        sendPassword.setOnClickListener(v -> {
            Query checkUser = databaseReference.child(user.getUid());
            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String password = snapshot.child("password").getValue(String.class);
                        if ((!Objects.equals(password, passwordTxt.getText().toString())
                                && !passwordInput.isErrorEnabled())
                                && !confirmInput.isErrorEnabled()) {

                            databaseReference.child(user.getUid()).child("password").setValue(passwordTxt.getText().toString());
                            user.updatePassword(passwordTxt.getText().toString())
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Log.d("Password modification:","User password updated.");
                                        }
                                    });

                            Toast.makeText(requireContext(), R.string.update_info, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        deleteButton.setOnClickListener(view -> deleteUser());

        return rootView;
    }

    private void startCropActivity() {
        ImagePicker.with(this)
                .crop(1, 1)                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri resultUri = data.getData();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(resultUri)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            avatarImage.setImageURI(user.getPhotoUrl());
                        }
                    });

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(getActivity(), ConnexionActivity.class));
            requireActivity().finish();
        }
    }

    private void initComponents(View rootView) {

        displayName = rootView.findViewById(R.id.namesLabelAccount);

        disconnectButton = rootView.findViewById(R.id.disconnectButtonAccount);
        sendInfoButton = rootView.findViewById(R.id.sendButtonAccount);
        sendPassword = rootView.findViewById(R.id.changePasswordButton);
        deleteButton = rootView.findViewById(R.id.deleteButton);

        avatarImage = rootView.findViewById(R.id.avatarImage);

        nameInput = rootView.findViewById(R.id.nameInputAccount);
        surnameInput = rootView.findViewById(R.id.surnameInputAccount);
        passwordInput = rootView.findViewById(R.id.passwordInputAccount);
        confirmInput = rootView.findViewById(R.id.confirmInputAccount);
        phoneInput = rootView.findViewById(R.id.phoneInputAccount);

        nameTxt = rootView.findViewById(R.id.nameTxtAccount);
        surnameTxt = rootView.findViewById(R.id.surnameTxtAccount);
        passwordTxt = rootView.findViewById(R.id.passwordTxtAccount);
        confirmPasswordTxt = rootView.findViewById(R.id.confirmTxtAccount);
        phoneTxt = rootView.findViewById(R.id.phoneTxtAccount);

        setupFloatingLabelError();
    }

    private void displayInfo() {

        Query checkUser = databaseReference.child(user.getUid());
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    String phone = snapshot.child("phoneNumber").getValue(String.class);
                    Uri avatarUri = user.getPhotoUrl();
                    String display = firstName + " " + lastName;

                    displayName.setText(display);
                    nameTxt.setText(firstName);
                    surnameTxt.setText(lastName);
                    phoneTxt.setText(phone);
                    Picasso.get().load(avatarUri).into(avatarImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    boolean isPhoneValid(CharSequence phone) {
        if (phone.length() != 10) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(phone).matches();
        }
    }

    private void setupFloatingLabelError() {

        Objects.requireNonNull(nameInput.getEditText()).addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0) {
                    nameInput.setError(getString(R.string.error_name));
                    nameInput.setErrorEnabled(true);
                } else {
                    nameInput.setErrorEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Objects.requireNonNull(surnameInput.getEditText()).addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0) {
                    surnameInput.setError(getString(R.string.error_surname));
                    surnameInput.setErrorEnabled(true);
                } else {
                    surnameInput.setErrorEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Objects.requireNonNull(phoneInput.getEditText()).addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0) {
                    phoneInput.setError(getString(R.string.error_phone));
                    phoneInput.setErrorEnabled(true);
                } else if (!isPhoneValid(phoneTxt.getText().toString())) {
                    phoneInput.setError(getString(R.string.error_phone_invalid));
                    phoneInput.setErrorEnabled(true);
                } else {
                    phoneInput.setErrorEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Objects.requireNonNull(passwordInput.getEditText()).addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (!passwordTxt.equals(confirmPasswordTxt)) {
                    confirmInput.setError(getString(R.string.error_confirm_password_invalid));
                    confirmInput.setErrorEnabled(true);
                }
                else {
                    confirmInput.setErrorEnabled(false);
                }
                if (text.length() == 0 ) {
                    passwordInput.setError(getString(R.string.error_password_required));
                    passwordInput.setErrorEnabled(true);
                }
                else if (text.length() < 6) {
                    passwordInput.setError(getString(R.string.error_password_invalid));
                    passwordInput.setErrorEnabled(true);
                }

                else {
                    passwordInput.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Objects.requireNonNull(confirmInput.getEditText()).addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0 ) {
                    confirmInput.setError(getString(R.string.error_confirm_password_required));
                    confirmInput.setErrorEnabled(true);
                }
                else if (!passwordTxt.getText().toString().equals(confirmPasswordTxt.getText().toString())){
                    confirmInput.setError(getString(R.string.error_confirm_password_invalid));
                    confirmInput.setErrorEnabled(true);
                }

                else {
                    confirmInput.setErrorEnabled(false);
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

    private void deleteUser() {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.delete_title))
                .setMessage(getString(R.string.delete_message))
                .setPositiveButton(getString(R.string.confirm), (dialogInterface, i) -> {
                    databaseReference.child(user.getUid()).removeValue();
                    user.delete().addOnCompleteListener(task -> checkUser());
                })
                .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.cancel())
                .create()
                .show();
    }
}
