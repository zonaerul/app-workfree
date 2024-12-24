package com.workfree;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.chaerulmobdev.data.adapter.data.Users;
import com.chaerulmobdev.data.modal.Sharedsave;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText email_input, pass_input;
    private AppCompatButton btnSubmit;
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth auth;
    private DatabaseReference db;
    private Sharedsave shared;
    private LinearLayout btn_login_with_google;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        shared = new Sharedsave(this);
        if(shared.getBoolean("logged")){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference("users");

        email_input = findViewById(R.id.email_input);
        pass_input = findViewById(R.id.pass_input);
        btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(v -> login());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Dapatkan dari Firebase
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btn_login_with_google = (LinearLayout)findViewById(R.id.linear_login_google);
        btn_login_with_google.setOnClickListener(v-> loginwithgoogle());

    }

    private void loginwithgoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("GoogleSignIn", "Google sign-in failed", e);
                Toast.makeText(this, "Login dengan Google gagal", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        Toast.makeText(this, "Login berhasil, Selamat datang " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                        shared.putAll(Map.of(
                                "email", user.getEmail(),
                                "logged", true
                        ));
                        
                        userSaveData(user);

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Autentikasi gagal", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void userSaveData(FirebaseUser user) {
        String id = user.getUid();
        Users users = new Users(
                user.getDisplayName(),
                user.getEmail(),
                "",
                "",
                "",
                "",
                "",
                "",
                true
        );

        db.child(id).setValue(users)
                .addOnSuccessListener(aVoid -> Log.i("FirebaseDatabase", "Data pengguna berhasil disimpan"))
                .addOnFailureListener(e -> Log.e("FirebaseDatabase", "Gagal menyimpan data pengguna", e));
    }

    private void login(){
        if(TextUtils.isEmpty(email_input.getText().toString()) && TextUtils.isEmpty(pass_input.getText().toString())){
            Toast.makeText(this, "input wajib di isi semua", Toast.LENGTH_SHORT).show();
        }

        auth.signInWithEmailAndPassword(email_input.getText().toString(), pass_input.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.i("FirebaseAuth", "Login berhasil");
                shared.putAll(Map.of(
                        "email", email_input.getText().toString(),
                        "logged", true
                ));
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
