package com.example.projet;

import android.widget.ImageView;

public class TournamentModel{


    public String nameTournament;
    public String startDate;
    public String endDate;
    public int numberPlayers;
    public int numberTeams;

    public TournamentModel(String tName, String start, String end, int player,int team) {

        nameTournament = tName;
        startDate = start;
        endDate = end;
        numberPlayers =  player;
        numberTeams =team;

    }

}
