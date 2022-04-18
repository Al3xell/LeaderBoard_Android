package com.example.projet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateTournamentActivity extends AppCompatActivity {

    private Button startDate;
    private Button endDate;

    private DatePickerDialog.OnDateSetListener startDatePickerDialog;
    private DatePickerDialog.OnDateSetListener endDatePickerDialog;

    private EditText nameTournamentTxt;
    private EditText numberTeamsTxt;
    private EditText numberPlayersTxt;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://database-tournament-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Tournaments");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tournament);

        startDate = findViewById(R.id.startDateButton);
        startDate.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(CreateTournamentActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    startDatePickerDialog,
                    year, month, day);
            dialog.getDatePicker().setMinDate(cal.getTimeInMillis());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
        startDatePickerDialog = (datePicker, year, month, day) -> {
            month = month + 1;
            startDate.setText(makeDate(day, month, year));
        };

        endDate = findViewById(R.id.endDateButton);
        endDate.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) ;
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(CreateTournamentActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    endDatePickerDialog,
                    year, month, day);
            dialog.getDatePicker().setMinDate((long) (cal.getTimeInMillis()+24*(3.6*Math.pow(10,6))));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
        endDatePickerDialog = (datePicker, year, month, day) -> {
            month = month + 1;
            endDate.setText(makeDate(day, month, year));
        };

        nameTournamentTxt = findViewById(R.id.nametournamentTxt);
        numberTeamsTxt = findViewById(R.id.numberTeamsTxt);
        numberPlayersTxt = findViewById(R.id.numberPlayerTxt);

        Button createButton = findViewById(R.id.createTournamentButton);

        createButton.setOnClickListener(view -> {
            try {
                checkInfo();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });


    }


    private String makeDate(int day, int month, int year) {
        String dayS = String.valueOf(day);
        String monthS = String.valueOf(month);
        if (day < 10) {
            dayS ="0"+dayS;
        }
        if (month < 10) {
            monthS = "0"+monthS;
        }
        return dayS + "/" + monthS + "/" + year;
    }

    private void checkExist(String name){
        databaseReference.orderByChild("nameTournament").equalTo(name.trim()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    String key = databaseReference.push().getKey();

                    String name = nameTournamentTxt.getText().toString().trim();
                    String startDateS = startDate.getText().toString();
                    String endDateS = endDate.getText().toString();
                    int numberTeams = Integer.parseInt(numberTeamsTxt.getText().toString());
                    int numberPlayers = Integer.parseInt(numberPlayersTxt.getText().toString());

                    TournamentModel tournamentModel = new TournamentModel(name, startDateS, endDateS, numberPlayers, numberTeams);

                    assert key != null;
                    databaseReference.child(key).setValue(tournamentModel);
                    finish();
                }
                else {
                    Toast.makeText(CreateTournamentActivity.this, getString(R.string.error_tournament_exist), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private boolean validateDates(CharSequence startDate, CharSequence endDate) throws ParseException {
        if (startDate.equals(getString(R.string.start_date)) || endDate.equals(getString(R.string.end_date))) {
            return false;
        }

        String[] startDateDecompose = String.valueOf(startDate).split("/");
        String[] endDateDecompose = String.valueOf(endDate).split("/");

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDateParse = sdf.parse(startDateDecompose[2]+"-"+startDateDecompose[1]+"-"+startDateDecompose[0]);
        Date endDateParse = sdf.parse(endDateDecompose[2]+"-"+endDateDecompose[1]+"-"+endDateDecompose[0]);

        assert endDateParse != null;
        int result = endDateParse.compareTo(startDateParse);
        return result > 0;
    }

    private void checkInfo() throws ParseException {

        if (!nameTournamentTxt.getText().toString().equals(" ")
                && !numberPlayersTxt.getText().toString().equals(" ")
                && !numberTeamsTxt.getText().toString().equals(" ")
                && validateDates(startDate.getText(), endDate.getText())) {
            checkExist(nameTournamentTxt.getText().toString());
        }
        else {
            Toast.makeText(this, getString(R.string.error_tournament_create_invalid), Toast.LENGTH_SHORT).show();
        }
    }

}
