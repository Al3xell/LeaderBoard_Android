package com.example.projet;


public class TournamentModel{


    public String nameTournament;
    public String startDate;
    public String endDate;
    public int numberPlayers;
    public int numberTeams;

    public TournamentModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public TournamentModel(String tName, String start, String end, int player,int team) {

        this.nameTournament = tName;
        this.startDate = start;
        this.endDate = end;
        this.numberPlayers =  player;
        this.numberTeams =team;

    }

}
