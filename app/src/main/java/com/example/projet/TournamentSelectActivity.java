package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.example.projet.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class TournamentSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_select);

        TabLayout tabs = findViewById(R.id.tournamentTabs);
        ViewPager viewPager = findViewById(R.id.viewPager);

        tabs.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new TournamentInfoFragment(), getString(R.string.about));
        viewPagerAdapter.addFragment(new TournamentTeamsFragment(), getString(R.string.board));
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}