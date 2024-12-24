package com.workfree;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.chaerulmobdev.data.modal.Sharedsave;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.workfree.fragment.HomeFragment;
import com.workfree.fragment.ProfileFragment;
import com.workfree.fragment.SearchFragment;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Sharedsave shared;

    private DatabaseReference database;
    private FrameLayout frameLayout;
    private BottomNavigationView bottomNavi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        auth = FirebaseAuth.getInstance();
        shared = new Sharedsave(this);
        database = FirebaseDatabase.getInstance().getReference("works");
        
        frameLayout = findViewById(R.id.framelayout);
        bottomNavi = findViewById(R.id.bottom_navi);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navi);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if(R.id.home_bottom == item.getItemId()){
                selectedFragment = new HomeFragment();
            }else if(R.id.search_bottom == item.getItemId()){
                selectedFragment = new SearchFragment();
            }else if(R.id.home_profile == item.getItemId()){
                selectedFragment = new ProfileFragment();
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framelayout, new HomeFragment())
                    .commit();
        }

    }


}