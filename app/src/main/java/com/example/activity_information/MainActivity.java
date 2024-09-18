package com.example.activity_information;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference usersRef;
    private Button button;
    private TextView userNameTextView, emailTextView, phoneNumTextView, genderTextView, birthdateTextView, provinceTextView, interestsTextView;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        user = auth.getCurrentUser();

        // Find views
        userNameTextView = findViewById(R.id.userNameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        phoneNumTextView = findViewById(R.id.phoneNumTextView);
        genderTextView = findViewById(R.id.genderTextView);
        birthdateTextView = findViewById(R.id.birthdateTextView);
        provinceTextView = findViewById(R.id.provinceTextView);
        interestsTextView = findViewById(R.id.interestsTextView);
        button = findViewById(R.id.log_out);



        // Check if user is logged in
        if (user == null) {
            // If the user is not logged in, redirect to the login activity
            Intent openLogIn = new Intent(getApplicationContext(), Login.class);
            startActivity(openLogIn);
            finish();
            return; // Ensure no further processing occurs if the user is not logged in
        }

        // Retrieve data from the Intent (if provided)
        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        String email = intent.getStringExtra("email");
        String phoneNum = intent.getStringExtra("phoneNum");
        String selectedGender = intent.getStringExtra("selectedGender");
        String birthdate = intent.getStringExtra("birthdate");
        String province = intent.getStringExtra("province");
        String interests = intent.getStringExtra("interests");

        if (userName != null && email != null) {
             //If data is passed via Intent, display it directly
            displayUserInfo(userName, email, phoneNum, selectedGender, birthdate, province, interests);
        } else {
            // If data is not passed, retrieve the data from Firebase
            getUser();
        }

        // Logout button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LogoutButton", "Logout button clicked");
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release any resources here if needed
    }

    // Method to display user info
    private void displayUserInfo(String userName, String email, String phoneNum, String selectedGender,
                                 String birthdate, String province, String interests) {
        userNameTextView.setText(userName);
        emailTextView.setText("Email: " + email);
        phoneNumTextView.setText("Phone Number: " + phoneNum);
        genderTextView.setText("Gender: " + selectedGender);
        birthdateTextView.setText("Birthdate: " + birthdate);
        provinceTextView.setText("Province: " + province);
        interestsTextView.setText("Interests: " + interests);
    }
    private void getUser(){

        try{
            String userId = auth.getCurrentUser().getUid();

            if (userId == null) {
                // Handle the case where userId is null
                Toast.makeText(MainActivity.this, "User ID is missing", Toast.LENGTH_SHORT).show();
                return;
            }

            usersRef.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            //Get the User object from Firebase
                            User userProfile = task.getResult().getValue(User.class);


                            if (userProfile != null) {
                                // Display the user info retrieved from Firebase

                                Log.i("Gender Debug", "currentGender" + userProfile.getGender());
                                displayUserInfo(userProfile.getUserName(), userProfile.getEmail(),
                                        userProfile.getPhoneNum(), userProfile.getGender(),
                                        userProfile.getBirthdate(), userProfile.getProvince(), userProfile.getInterests());


                            }
                        } else {
                            Toast.makeText(MainActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        catch (Exception e) {
            Toast.makeText(this, "An error occured while getting user", Toast.LENGTH_SHORT).show();
        }

    }
}

