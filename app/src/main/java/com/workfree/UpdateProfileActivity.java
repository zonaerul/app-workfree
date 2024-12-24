package com.workfree;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.chaerulmobdev.data.adapter.data.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateProfileActivity extends AppCompatActivity {
    private EditText name_input, email_input, phone_input, pendidikan_input, location_input, magang_input, skill_input, tgl_input;
    private AppCompatButton btnSubmit;
    private DatabaseReference db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile_activity);

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference("users");

        // Inisialisasi komponen UI
        name_input = findViewById(R.id.name_input);
        pendidikan_input = findViewById(R.id.pendidikan_input);
        email_input = findViewById(R.id.email_input);
        phone_input = findViewById(R.id.phone_input);
        location_input = findViewById(R.id.location_input);
        magang_input = findViewById(R.id.magang_input);
        skill_input = findViewById(R.id.skill_input);
        tgl_input = findViewById(R.id.tanggal_lahir_input);
        btnSubmit = findViewById(R.id.btn_submit);

        // Tambahkan listener untuk tombol submit
        btnSubmit.setOnClickListener(v -> updateProfile());
        load_profile();
    }

    private void load_profile() {
        if (auth != null) {
            String userId = auth.getUid();
            if (userId != null) {
                db.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Mendapatkan data pengguna dari database
                            String name = dataSnapshot.child("name").getValue(String.class);
                            String emailValue = dataSnapshot.child("email").getValue(String.class);
                            String phoneValue = dataSnapshot.child("number").getValue(String.class);
                            String location = dataSnapshot.child("location").getValue(String.class);
                            String birthDate = dataSnapshot.child("tanggalLahir").getValue(String.class);
                            String magang_text = dataSnapshot.child("magang").getValue(String.class);
                            String skill_text = dataSnapshot.child("skill").getValue(String.class);
                            String pendidikan_text = dataSnapshot.child("pendidikan").getValue(String.class);

                            // Menampilkan data ke TextView
                            name_input.setText(name != null ? name : "");
                            email_input.setText(emailValue != null ? emailValue : "");
                            phone_input.setText(!phoneValue.isEmpty() ? phoneValue : "");
                            location_input.setText(!location.isEmpty() ? location : "");
                            magang_input.setText(magang_text != null ? magang_text : "");
                            skill_input.setText(!skill_text.isEmpty()  ? skill_text : "");
                            tgl_input.setText(!birthDate.isEmpty()  ? birthDate : "");
                            pendidikan_input.setText(!pendidikan_text.isEmpty()  ? pendidikan_text : "");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                       System.out.println("gagal memaut data profile");
                    }
                });
            } else {
                System.out.println("UID tidak ditemukan");
            }
        } else {
            System.out.println("Pengguna tidak terautentikasi");
        }
    }

    private void updateProfile() {
        // Ambil input dari pengguna
        String name = name_input.getText().toString().trim();
        String pendidikan = pendidikan_input.getText().toString().trim();
        String email = email_input.getText().toString().trim();
        String phone = phone_input.getText().toString().trim();
        String location = location_input.getText().toString().trim();
        String magang = magang_input.getText().toString().trim();
        String skill = skill_input.getText().toString().trim();
        String birthDate = tgl_input.getText().toString().trim();

        // Validasi input wajib
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Nama harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Nomor telepon harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Dapatkan UID pengguna
        String userId = auth.getUid();
        if (userId == null) {
            Toast.makeText(this, "Pengguna tidak terautentikasi. Silakan login kembali.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buat objek pengguna
        Users userProfile = new Users(
                name,
                email,
                phone,
                location,
                skill,
                pendidikan,
                birthDate,
                magang,
                true
        );

        // Simpan data ke Firebase
        db.child(userId).setValue(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    finish(); // Kembali ke aktivitas sebelumnya
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal memperbarui profil: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });


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
