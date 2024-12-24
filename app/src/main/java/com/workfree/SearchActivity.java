package com.workfree;

import static android.content.ContentValues.TAG;
import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chaerulmobdev.data.adapter.HomeAdapter;
import com.chaerulmobdev.data.adapter.data.WorkData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private String gaji,lokasi;
    private FirebaseAuth auth;
    private DatabaseReference db;
    RecyclerView recycler;
    private ArrayList<WorkData> array;
    private HomeAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference("works");

        // Mendapatkan data dari Intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            gaji = bundle.getString("gaji", "");
            lokasi = bundle.getString("lokasi", "");
            if (!TextUtils.isEmpty(gaji) && !TextUtils.isEmpty(lokasi)) {
                searchWork(gaji, lokasi);
            } else {
                Toast.makeText(this, "Kata kunci pencarian tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            }
        }

        recycler = findViewById(R.id.recycler_search);

        // Atur RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recycler.setLayoutManager(gridLayoutManager);
        array = new ArrayList<WorkData>();
        adapter = new HomeAdapter(this, array);
        recycler.setAdapter(adapter);
    }
    private void searchWork(String gaji, String lokasi) {
        // Hapus karakter non-numerik dari harga
        int minPrice;
        try {
            minPrice = Integer.parseInt(gaji.replaceAll("[^\\d]", ""));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Format gaji tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        // Query berdasarkan lokasi
        db.orderByChild("location").equalTo(lokasi).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                array.clear(); // Bersihkan data sebelumnya
                if (dataSnapshot.exists()) {
                    array.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()){
                        try {
                            String name = data.child("name").getValue(String.class);
                            String price = data.child("price").getValue(String.class);
                            String image = data.child("image").getValue(String.class);
                            String location = data.child("location").getValue(String.class);

                            if (name != null && price != null && image != null && location != null) {
                                WorkData workData = new WorkData(name, price, image, location);
                                array.add(workData);
                            } else {
                                Log.w(TAG, "Data tidak lengkap untuk key: " + data.getKey());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Kesalahan parsing data untuk key: " + data.getKey(), e);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Gagal mencari data: " + databaseError.getMessage());
                Toast.makeText(SearchActivity.this, "Terjadi kesalahan saat mencari.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fungsi untuk menyarankan lokasi terdekat
    private void suggestClosestLocations(String lokasi, int minPrice) {
        db.orderByChild("location").equalTo(lokasi).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()){
                        try {
                            String name = data.child("name").getValue(String.class);
                            String price = data.child("price").getValue(String.class);
                            String image = data.child("image").getValue(String.class);
                            String location = data.child("location").getValue(String.class);

                            if (name != null && price != null && image != null && location != null) {
                                WorkData workData = new WorkData(name, price, image, location);
                                array.add(workData);
                            } else {
                                Log.w(TAG, "Data tidak lengkap untuk key: " + data.getKey());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Kesalahan parsing data untuk key: " + data.getKey(), e);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Gagal mencari data lokasi: " + databaseError.getMessage());
            }
        });
    }



    // Fungsi untuk menghitung jarak Levenshtein
    private int calculateLevenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1));
                }
            }
        }
        return dp[a.length()][b.length()];
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }
}
