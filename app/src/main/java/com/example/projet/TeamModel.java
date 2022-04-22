package com.example.projet;

import java.util.ArrayList;

public class TeamModel {
    private final int maxPlayers;
    private final String nameTeam;
    private final ArrayList<String> players;
    private final String imageURI;

    TeamModel(int maxPlayers, String nameTeam, ArrayList<String> players, String imageURI) {
        this.maxPlayers = maxPlayers;
        this.nameTeam = nameTeam;
        this.players  = players;
        this.imageURI = imageURI;
    }

    public int getMaxPlayers() { return this.maxPlayers; }
    public String getNameTeam() {
        return this.nameTeam;
    }
    public ArrayList<String> getPlayers() { return this.players; }
    public String getImageURI() { return this.imageURI; }
}
