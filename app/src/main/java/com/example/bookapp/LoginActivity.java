package com.example.bookapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    // Progress dialog
    private ProgressDialog progressDialog;

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
        emailEt = findViewById(R.id.emailEditText); // Replace with the correct ID from your layout
        passwordEt = findViewById(R.id.passwordEditText);
        registerBtn = findViewById(R.id.registerButton);
        loginBtn = findViewById(R.id.loginButton); // Ensure this ID matches the layout

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Set up progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        // Handle register button click
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to RegisterActivity
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
            // Invalid email
            Toast.makeText(this, "Invalid email pattern!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            // Empty password
            Toast.makeText(this, "Enter password!", Toast.LENGTH_SHORT).show();
        } else {
            // Data is valid, proceed to login
            loginUser();
        }
    }

    private void loginUser() {
        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        // Sign in using Firebase Auth
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Check user role after successful login
                        checkUser();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Login failed
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUser() {
        // Get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            // Check the user role in the database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Get user role from the database
                            String userRole = "" + snapshot.child("userRole").getValue();

                            if ("User".equals(userRole)) {
                                // Redirect to user dashboard

                                finish();
                            } else if ("Admin".equals(userRole)) {
                                // Redirect to admin dashboard

                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Database error handling
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
