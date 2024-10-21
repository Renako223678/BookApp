package com.example.bookapp.Authentication;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Button registerButton;
    private ImageButton backButton;
    private EditText name, email, password, repassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize views
        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);
        name = findViewById(R.id.nameEt);
        email = findViewById(R.id.emailEditText);           // Initialize the email field here
        password = findViewById(R.id.passwordEditText);
        repassword = findViewById(R.id.confirmPasswordEditText);

        // Back button logic
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Register button logic
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void validateData() {
        String valiName = name.getText().toString().trim();
        String valiEmail = email.getText().toString().trim();
        String valiPassword = password.getText().toString().trim();
        String valiRepassword = repassword.getText().toString().trim();

        if (TextUtils.isEmpty(valiName)) {
            Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(valiEmail).matches()) {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(valiPassword)) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
        } else if (!valiPassword.equals(valiRepassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else {
            createUser(valiEmail, valiPassword);
        }
    }

    private void createUser(String email, String password) {
        // Show progress (implement ProgressDialog if needed)

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // User created
                        updateUser(authResult.getUser().getUid());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUser(String uid) {
        long timestamp = System.currentTimeMillis();

        // Setup data for Firebase
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("email", email.getText().toString());
        hashMap.put("name", name.getText().toString());
        hashMap.put("profileImage", "");
        hashMap.put("userType", "user");
        hashMap.put("timestamp", timestamp);

        // Set data to database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(RegisterActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
