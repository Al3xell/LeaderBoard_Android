package com.example.projet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class TeamModel implements Serializable {
    public int maxPlayers;
    public String nameTeam;
    public HashMap<String, UserModel> players;
    public String imageURI;

    public TeamModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public TeamModel(int maxPlayers, String nameTeam, HashMap<String, UserModel> players, String imageURI) {
        this.maxPlayers = maxPlayers;
        this.nameTeam = nameTeam;
        this.players  = players;
        this.imageURI = imageURI;
    }

    public int getMaxPlayers() { return this.maxPlayers; }
    public String getNameTeam() {
        return this.nameTeam;
    }
    public HashMap<String, UserModel> getPlayers() { return this.players; }
    public String getImageURI() { return this.imageURI; }
}
