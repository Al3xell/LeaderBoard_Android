package com.example.projet;


import java.util.Locale;

public class TournamentModel{


    public String nameTournament;
    public String nameTournamentLower;
    public String startDate;
    public String endDate;
    public String admin;
    public int numberPlayers;
    public int numberTeams;

    public TournamentModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public TournamentModel(String tName, String start, String admin, String end, int player,int team) {

        this.nameTournament = tName;
        this.nameTournamentLower = tName.toLowerCase(Locale.ROOT);
        this.startDate = start;
        this.endDate = end;
        this.admin = admin;
        this.numberPlayers =  player;
        this.numberTeams =team;

    }

}
