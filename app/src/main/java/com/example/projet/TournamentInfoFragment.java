package com.example.projet;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class TournamentInfoFragment extends Fragment {

    private TournamentModel tournamentModel;
    public String keyTournament;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private final DatabaseReference tournamentRef = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Tournaments");
    private final DatabaseReference userRef = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");

    public TournamentInfoFragment() {
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
        View view = inflater.inflate(R.layout.fragment_tournament_info, container, false);

        TextView nameTournamentTitle = view.findViewById(R.id.tournamentName);
        TextView numberTeams = view.findViewById(R.id.numberTeamsLabel);
        TextView startDateLabel = view.findViewById(R.id.startDateLabel);
        TextView endDateLabel = view.findViewById(R.id.endDateLabel);

        nameTournamentTitle.setText(tournamentModel.nameTournament);
        startDateLabel.setText(tournamentModel.startDate);
        endDateLabel.setText(tournamentModel.endDate);

        tournamentRef.orderByChild("nameTournament").equalTo(tournamentModel.nameTournament).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if(ds.exists()) {
                        int numberTeamIn = (int) ds.child("Teams").getChildrenCount();
                        String teams = numberTeamIn +"/"+tournamentModel.numberTeams;
                        setKey(ds.getKey());
                        numberTeams.setText(teams);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Button leaveButton = view.findViewById(R.id.leaveButton);
        Button deleteButton = view.findViewById(R.id.deleteTournament);

        leaveButton.setOnClickListener(view1 -> {
            assert user != null;
            userRef.child(user.getUid()).child("tournamentsIn").child(getKey()).removeValue();
            tournamentRef.child(getKey()).child("players").child(user.getUid()).removeValue();
            startActivity(new Intent(requireActivity(), MainActivity.class));
            requireActivity().finish();
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRef.orderByChild("tournamentsIn").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            userRef.child(Objects.requireNonNull(ds.getKey())).child("tournamentsIn").child(getKey()).removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                tournamentRef.child(getKey()).removeValue();
                startActivity(new Intent(requireActivity(), MainActivity.class));
                requireActivity().finish();
            }
        });


        return view;
    }

    public void setTournament(TournamentModel tournament) {
        this.tournamentModel = tournament;
    }
    public void setKey(String key) {
        this.keyTournament = key;
    }
    public String getKey() {
        return this.keyTournament;
    }
}