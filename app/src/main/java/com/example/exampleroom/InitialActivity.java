package com.example.exampleroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.example.exampleroom.ui.HomeFragment;
import com.example.exampleroom.ui.RegisterFragment;

public class InitialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        HomeFragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = new HomeFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.initialcontainer, fragment)
                .commit();
    }
}