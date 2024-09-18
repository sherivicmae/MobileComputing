package com.example.activity_information;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    Button button_logIn;
    FirebaseAuth mAuth;
    TextView textView;

    @SuppressLint("WrongViewCast")
    @Override
    public void onStart() {
        super.onStart();
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextEmail= findViewById(R.id.logIn_email);
        editTextPassword = findViewById(R.id.logIn_password);
        button_logIn = findViewById(R.id.button_logIn);
        textView=findViewById(R.id.signIn_now);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Register.class);
                startActivity(intent);
                finish();
            }
        });

        button_logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                // Validate email address using the validateEmailAddress method
                if (!validateEmailAddress(editTextEmail)) {
                    return;  // Stop execution if the email is invalid
                }

                // Check if password is empty
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Sign in the user using Firebase Authentication
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        String userId = firebaseUser.getUid();

                                        Log.d("Login", "UserID retrieved: " + userId);
                                        // Pass the userId to the MainActivity
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.putExtra("userId", userId);  // Pass userId to the MainActivity
                                        startActivity(intent);
                                    }
                                    Toast.makeText(getApplicationContext(), "Login Successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


    }
    private boolean validateEmailAddress(EditText editTextEmail) {
        String emailInput = editTextEmail.getText().toString();

        // Check if email is not empty and follows the correct pattern
        if (!emailInput.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            return true;  // Email is valid
        } else {
            Toast.makeText(this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
            return false;  // Email is invalid
        }
    }
}