package com.workfree.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.workfree.R;
import com.workfree.UpdateProfileActivity;

public class ProfileFragment extends Fragment {

    private FirebaseAuth auth;
    private View view;
    private DatabaseReference db;
    private TextView username;
    private TextView user;
    private TextView email;
    private TextView phone;
    private TextView lokasi;
    private TextView tgk_lahir;
    private TextView magang;
    private TextView minat;
    private TextView pendidikan;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_fragment, container, false);
        auth =FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference("users");
        username = view.findViewById(R.id.user_profile);
        user = view.findViewById(R.id.user_name_profile);
        email = view.findViewById(R.id.email_profile);
        phone = view.findViewById(R.id.phone_user);
        lokasi = view.findViewById(R.id.lokasi_profile);
        tgk_lahir = view.findViewById(R.id.tgl_profile);
        magang = view.findViewById(R.id.magang_profile);
        minat = view.findViewById(R.id.minat_skill_profile);
        pendidikan = view.findViewById(R.id.pendidikan_profile);
        ImageView btn_edit = view.findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(v->{
            Intent intent = new Intent(getContext(), UpdateProfileActivity.class);
            startActivity(intent);

        });

        load_profile();
        return view;
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
                            username.setText(name != null ? name : "Tidak diketahui");
                            user.setText(name != null ? name : "Tidak diketahui");
                            email.setText(emailValue != null ? emailValue : "Tidak diketahui");
                            phone.setText(!phoneValue.isEmpty() ? phoneValue : "Tidak diketahui");
                            lokasi.setText(!location.isEmpty() ? location : "Tidak diketahui");
                            tgk_lahir.setText(birthDate != null ? birthDate : "Tidak diketahui");
                            magang.setText(!magang_text.isEmpty()  ? magang_text : "Tidak diketahui");
                            minat.setText(!skill_text.isEmpty()  ? skill_text : "Tidak diketahui");
                            pendidikan.setText(!pendidikan_text.isEmpty()  ? pendidikan_text : "Tidak diketahui");
                        } else {
                            // Jika data pengguna tidak ditemukan
                            username.setText("Pengguna tidak ditemukan");
                            user.setText("-");
                            email.setText("-");
                            phone.setText("-");
                            lokasi.setText("-");
                            tgk_lahir.setText("-");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Penanganan kesalahan database
                        username.setText("Gagal memuat profil");
                    }
                });
            } else {
                username.setText("UID tidak ditemukan");
            }
        } else {
            username.setText("Pengguna tidak terautentikasi");
        }
    }

}
