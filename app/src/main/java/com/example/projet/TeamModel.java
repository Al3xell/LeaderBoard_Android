package com.example.projet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class TeamModel implements Serializable {
    public int maxPlayers;
    public String id;
    public String nameTeam;
    public String admin;
    public HashMap<String, UserModel> players;
    public String imageURI;

    public TeamModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public TeamModel(int maxPlayers, String id, String nameTeam,String admin, HashMap<String, UserModel> players, String imageURI) {
        this.maxPlayers = maxPlayers;
        this.id = id;
        this.nameTeam = nameTeam;
        this.admin = admin;
        this.players  = players;
        this.imageURI = imageURI;
    }

    public int getMaxPlayers() { return this.maxPlayers; }
    public String getId() { return this.id; }
    public String getNameTeam() {
        return this.nameTeam;
    }
    public String getAdmin() { return this.admin; }
    public HashMap<String, UserModel> getPlayers() { return this.players; }
    public String getImageURI() { return this.imageURI; }
}
