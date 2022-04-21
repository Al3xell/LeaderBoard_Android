package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.example.projet.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.io.Serializable;

public class TournamentSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_select);

        Intent mIntent = getIntent();
        TournamentModel tournamentModel = (TournamentModel) mIntent.getSerializableExtra("Tournament");

        Toolbar toolbar = findViewById(R.id.toolbarInfo);
        toolbar.setTitle(tournamentModel.nameTournament);
        setSupportActionBar(toolbar);

        TabLayout tabs = findViewById(R.id.tournamentTabs);
        ViewPager viewPager = findViewById(R.id.viewPager);

        tabs.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        TournamentInfoFragment tournamentInfoFragment = new TournamentInfoFragment();
        tournamentInfoFragment.setTournament(tournamentModel);
        TournamentTeamsFragment tournamentTeamsFragment = new TournamentTeamsFragment();
        tournamentTeamsFragment.setTournament(tournamentModel);
        viewPagerAdapter.addFragment(tournamentInfoFragment, getString(R.string.about));
        viewPagerAdapter.addFragment(tournamentTeamsFragment, getString(R.string.board));
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}