package com.workfree.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chaerulmobdev.data.adapter.HomeAdapter;
import com.chaerulmobdev.data.adapter.data.WorkData;
import com.chaerulmobdev.data.modal.Sharedsave;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.workfree.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private Sharedsave shared;
    private View view;
    private DatabaseReference database;
    private RecyclerView recycler;
    private ArrayList<WorkData> array;
    private ArrayList<WorkData> filteredArray; // Tambahan untuk data hasil pencarian
    private HomeAdapter adapter;

    private static final String TAG = "HomeFragment";
    private static final String WORKS_PATH = "works";
    private EditText search_input;
    private TextView user_home;
    private DatabaseReference userDatabase;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment, container, false);

        // Inisialisasi
        shared = new Sharedsave(requireContext());
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference(WORKS_PATH);
        userDatabase = FirebaseDatabase.getInstance().getReference("users");
        recycler = view.findViewById(R.id.recycler_home);
        search_input = view.findViewById(R.id.search_input);
        user_home = view.findViewById(R.id.user_home);
        loadUser();

        // Atur RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recycler.setLayoutManager(gridLayoutManager);
        array = new ArrayList<>();
        filteredArray = new ArrayList<>(); // Inisialisasi array hasil pencarian
        adapter = new HomeAdapter(requireContext(), filteredArray);
        recycler.setAdapter(adapter);

        // Listener untuk pencarian
        search_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter_data(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Muat data dari Firebase
        load_data();

        return view;
    }

    private void load_data() {
        if (shared.getBoolean("logged")) {
            database.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        array.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
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
                        filteredArray.clear();
                        filteredArray.addAll(array);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Data tidak ditemukan di path: " + WORKS_PATH);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Database error: " + databaseError.getMessage(), databaseError.toException());
                }
            });
        } else {
            Log.w(TAG, "Pengguna belum login.");
        }
    }

    private void loadUser(){
        userDatabase.child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    user_home.setText(dataSnapshot.child("name").getValue(String.class));
                }else{
                    user_home.setText("Failed...");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }

    private void filter_data(String keyword) {
        filteredArray.clear();
        if (keyword.isEmpty()) {
            // Jika pencarian kosong, tampilkan semua data
            filteredArray.addAll(array);
        } else {
            // Filter data berdasarkan nama
            for (WorkData data : array) {
                if (data.getName().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredArray.add(data);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
