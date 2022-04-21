package com.example.projet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TournamentTeamsFragment extends Fragment {

    private TournamentModel tournamentModel;

    public TournamentTeamsFragment() {
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
        return inflater.inflate(R.layout.fragment_tournament_teams, container, false);
    }

    public void setTournament(TournamentModel tournament) {
        this.tournamentModel = tournament;
    }
}