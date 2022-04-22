package com.example.projet;

import java.util.ArrayList;
import java.util.HashMap;

public class TeamModel {
    private int maxPlayers;
    private String nameTeam;
    private HashMap<String, HashMap<String, String>> players;
    private String imageURI;

    public TeamModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public TeamModel(int maxPlayers, String nameTeam, HashMap<String, HashMap<String, String>> players, String imageURI) {
        this.maxPlayers = maxPlayers;
        this.nameTeam = nameTeam;
        this.players  = players;
        this.imageURI = imageURI;
    }

    public int getMaxPlayers() { return this.maxPlayers; }
    public String getNameTeam() {
        return this.nameTeam;
    }
    public HashMap<String, HashMap<String, String>> getPlayers() { return this.players; }
    public String getImageURI() { return this.imageURI; }
}
