package com.example.bookapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    // Firebase Auth
    private FirebaseAuth firebaseAuth;

    // ProgressBar
    private ProgressBar progressBar;

    // EditText fields for email and password
    private EditText emailEt, passwordEt;

    // Buttons for login and register
    private Button registerBtn, loginBtn;

    private String email = "", password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Initialize views
        emailEt = findViewById(R.id.emailEditText);
        passwordEt = findViewById(R.id.passwordEditText);
        registerBtn = findViewById(R.id.registerButton);
        loginBtn = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar); // Initialize the ProgressBar

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Handle register button click
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        // Handle login button click
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void validateData() {
        // Get data
        email = emailEt.getText().toString().trim();
        password = passwordEt.getText().toString().trim();

        // Validate email and password
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email pattern!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter password!", Toast.LENGTH_SHORT).show();
        } else {
            loginUser(); // Valid data, proceed with login
        }
    }

    private void loginUser() {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        checkUser(); // Success, check user type
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Hide progress bar and show error
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Hide progress bar
                            progressBar.setVisibility(View.GONE);

                            String userRole = "" + snapshot.child("userRole").getValue();
                            if ("user".equals(userRole)) {
                                Intent intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                                startActivity(intent);
                            } else if ("admin".equals(userRole)) {
                                startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                            }
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Hide progress bar and show error
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
