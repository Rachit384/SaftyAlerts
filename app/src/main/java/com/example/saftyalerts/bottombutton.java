package com.example.saftyalerts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.saftyalerts.Fragments.Profile;
import com.example.saftyalerts.Fragments.Chat;
import com.example.saftyalerts.Fragments.HomeFragment;
import com.example.saftyalerts.Fragments.PostFragment;
import com.example.saftyalerts.Fragments.Search;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class bottombutton extends AppCompatActivity {


    BottomNavigationView bnview;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bottombutton);



        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.container, new HomeFragment());

        ft.commit();

        bnview = findViewById(R.id.bottom);

       bnview.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.home) {

                    loadFragment(new HomeFragment());


                } else if (id == R.id.search) {
                    loadFragment(new Search());
                } else if (id == R.id.post) {
                    loadFragment(new PostFragment());
                } else if (id == R.id.chat) {
                    loadFragment(new Chat());
                } else {
                    loadFragment(new Profile());
                }

                return true;
            }
        });


        bnview.setSelectedItemId(R.id.home);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

            ft.replace(R.id.container, fragment);

        ft.commit();

    }
}