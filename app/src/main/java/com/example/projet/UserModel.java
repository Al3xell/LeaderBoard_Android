package com.example.projet;

import java.io.Serializable;
import java.util.HashMap;

public class UserModel implements Serializable {

    public String id;
    public String email;
    public String firstName;
    public String lastName;
    public String password;
    public String phoneNumber;
    public String uri;
    public boolean inTeam;
    public HashMap<String, TournamentModel> tournamentsIn;

    public UserModel() {}

    public UserModel(String id, String email, String firstName, String lasName, String password, String phoneNumber, String uri) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lasName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.uri = uri;
        this.tournamentsIn = new HashMap<>();
    }

    public String getId() { return this.id; }
    public String getEmail() { return this.email; }
    public String getFirstName() { return this.firstName; }
    public String getLastName() { return this.lastName; }
    public String getPassword() { return this.password; }
    public String getPhoneNumber() { return this.phoneNumber; }
    public String getUri() { return this.uri; }
    public HashMap<String, TournamentModel> getTournamentsIn() { return this.tournamentsIn; }

}
