package com.jkutkut.firebaseauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity
    implements View.OnClickListener {

    private Button btnLogin;
    private Button btnSignup;
    private EditText etEmail;
    private EditText etPassword;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();

        btnLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);

        btnLogin.setEnabled(firebaseUser != null);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnLogin)
            login();
        else
            signup();
    }

    private String[] validate() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            Snackbar.make(etEmail, "Email is required", Snackbar.LENGTH_LONG).show();
        }
        else if (!email.matches("^[a-z][a-z1-9._-]*@[a-z]+\\.[a-z]{1,3}$")) {
            Snackbar.make(etEmail, "Invalid email", Snackbar.LENGTH_LONG).show();
        }
        else if (password.isEmpty()) {
            Snackbar.make(etPassword, "Password is required", Snackbar.LENGTH_LONG).show();
        }
        else
            return new String[] {email, password};
        return null;
    }

    private void login() {
        String[] credentials = validate();
        if (credentials == null)
            return;
        String email = credentials[0];
        String password = credentials[1];

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseUser = firebaseAuth.getCurrentUser();
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else {
                            Snackbar.make(btnLogin, "Login failed", Snackbar.LENGTH_LONG).show();
                            System.out.println(task.getResult().getAdditionalUserInfo());
                        }
                    }
                });
    }

    private void signup() {
        String[] credentials = validate();
        if (credentials == null)
            return;
        String email = credentials[0];
        String password = credentials[1];

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        firebaseUser = firebaseAuth.getCurrentUser();
                        Snackbar.make(btnLogin, "Login successful", Snackbar.LENGTH_LONG).show();
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else {
                        Snackbar.make(btnLogin, "User not registered.", Snackbar.LENGTH_LONG).show();
                    }
                }
            });
    }
}