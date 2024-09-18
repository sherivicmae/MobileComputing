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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    EditText editTextEmail, editTextPassword, editTextUser,editTextPhoneNum;
    Button nextBtn;
    FirebaseAuth mAuth;
    TextView textView;

    @SuppressLint("WrongViewCast")
    @Override
    public void onStart() {
        super.onStart();

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
        mAuth=FirebaseAuth.getInstance();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextUser = findViewById(R.id.inputUsername);
        editTextEmail= findViewById(R.id.inputEmail);
        editTextPassword = findViewById(R.id.inputPassword);
        nextBtn = findViewById(R.id.button_next);
        textView=findViewById(R.id.already_account);
        editTextPhoneNum = findViewById(R.id.inputPhoneNum);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String userName, email, password, phoneNum;
                    userName = String.valueOf(editTextUser.getText());
                    email = String.valueOf(editTextEmail.getText());
                    password = String.valueOf(editTextPassword.getText());
                    phoneNum = String.valueOf(editTextPhoneNum.getText());

                    // Validate input fields
                    if (TextUtils.isEmpty(userName)) {
                        Toast.makeText(Register.this, "Enter username", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(password)) {
                        Toast.makeText(Register.this, "Enter password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Check if the password has the minimum required length (e.g., 6 characters)
                    if (password.length() < 6) {
                        Toast.makeText(Register.this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(Register.this, "Enter email", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!validateEmailAddress(editTextEmail)) {
                        return;  // Stop execution if the email is invalid
                    }
                    if (TextUtils.isEmpty(phoneNum)) {
                        Toast.makeText(Register.this, "Enter phone number", Toast.LENGTH_SHORT).show();
                        return;
                    }

                // Pass data to Register2 activity
                Intent intent = new Intent(Register.this, Register2.class);
                intent.putExtra("username", userName);
                intent.putExtra("email", email);
                intent.putExtra("password", password);
                intent.putExtra("phoneNum", phoneNum);
                startActivity(intent);

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