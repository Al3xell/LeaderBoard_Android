package com.example.projet;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AccountFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private Button disconnectButton;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        user = firebaseAuth.getCurrentUser();

        initComponents(rootView);

        disconnectButton.setOnClickListener(view -> {
            firebaseAuth.signOut();
            checkUser();
        });

        displayInfo();


        ActivityResultLauncher<String> mTakePhoto = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> avatarImage.setImageURI(result));

        avatarImage.setOnClickListener(v -> {
            /*if(ContextCompat.checkSelfPermission(getActivity().this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
            PackageManager.per)*/
        });


        return rootView;
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
    }

    private void displayInfo() {

        Query checkUser = databaseReference.child(user.getUid());
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    String phone = snapshot.child("phoneNumber").getValue(String.class);
                    String display = firstName +" "+ lastName;

                    displayName.setText(display);
                    nameTxt.setText(firstName);
                    surnameTxt.setText(lastName);
                    phoneTxt.setText(phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}