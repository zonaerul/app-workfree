package com.workfree.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import com.workfree.R;
import com.workfree.SearchActivity;

public class SearchFragment extends Fragment {

    private View view;
    private EditText gaji, lokasi, skill;
    private Button btnSubmit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        gaji = view.findViewById(R.id.gaji_input);
        lokasi = view.findViewById(R.id.lokasi_input);
        skill = view.findViewById(R.id.skill_input);
        btnSubmit = view.findViewById(R.id.btn_search);
        btnSubmit.setOnClickListener(v->search(gaji.getText().toString(),lokasi.getText().toString()));
        return view;
    }

    private void search(String gaji, String lokasi) {
        Intent intent = new Intent(getContext(), SearchActivity.class);
        intent.putExtra("gaji", gaji);
        intent.putExtra("lokasi", lokasi);
        startActivity(intent);

    }
}
