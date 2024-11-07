package com.example.activity_information;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {

    ImageButton profile,hello,calculator,bluetooth,signout, studies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        profile = findViewById(R.id.btn_profile);
        hello=findViewById(R.id.btn_hello);
        calculator=findViewById(R.id.btn_calculator);
        signout=findViewById(R.id.btn_signout);
        bluetooth=findViewById(R.id.btn_bluetooth);
        studies = findViewById(R.id.btn_study);

        if (profile != null) {
            profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentProfile = new Intent(getApplicationContext(), MainActivity.class);
                    intentProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentProfile);
                    finish();
                }
            });
        }

        hello.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intentHello = new Intent(getApplicationContext(), HelloWorld.class);
                intentHello.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentHello);
                finish();
            }
        });

        calculator.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intentCalcu = new Intent(getApplicationContext(), Calculator.class);
                intentCalcu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentCalcu);
                finish();
            }
        });

        studies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentStudy = new Intent(getApplicationContext(), StudyHome.class);
                intentStudy.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentStudy);
                finish();
            }
        });

        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentBluetooth = new Intent(getApplicationContext(), BluetoothTransfer.class);
                intentBluetooth.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentBluetooth);
                finish();
            }
        });


        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("LogoutButton", "Logout button clicked");
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(getApplicationContext(), Login.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();

                
            }
        });
    }


}