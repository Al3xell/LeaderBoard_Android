package com.example.projet;


import com.firebase.ui.auth.data.model.User;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;

public class TournamentModel implements Serializable {


    public String nameTournament;
    public String nameTournamentLower;
    public String startDate;
    public String endDate;
    public String admin;
    public int numberPlayers;
    public int numberTeams;
    public HashMap<String, UserModel> players;
    public HashMap<String, TeamModel> teams;

    public TournamentModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public TournamentModel(String tName, String start, String end,String admin, int player,int team) {

        this.nameTournament = tName;
        this.nameTournamentLower = tName.toLowerCase(Locale.ROOT);
        this.startDate = start;
        this.endDate = end;
        this.admin = admin;
        this.numberPlayers =  player;
        this.numberTeams =team;
        this.players = new HashMap<>();
        this.teams = new HashMap<>();
    }

    public String getNameTournament() { return this.nameTournament; }
    public String getNameTournamentLower() { return this.nameTournamentLower; }
    public String getStartDate() { return this.startDate; }
    public String getEndDate() { return this.endDate; }
    public String getAdmin() { return this.admin; }
    public int getNumberPlayers() { return this.numberPlayers; }
    public int getNumberTeams() { return this.numberTeams; }
    public HashMap<String, UserModel> getPlayers() { return this.players; }
    public HashMap<String, TeamModel> getTeams() { return this.teams; }
}
