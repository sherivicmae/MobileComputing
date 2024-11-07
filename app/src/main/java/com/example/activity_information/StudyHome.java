package com.example.activity_information;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StudyHome extends AppCompatActivity {

    Button Infrared, BTTransfer, BTWireless,BackHome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_study_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Infrared = findViewById(R.id.btn_InfraredStudy);
        BTTransfer = findViewById(R.id.btn_BTFileTransfer);
        BTWireless = findViewById(R.id.btn_BTWirelessTransfer);
        BackHome = findViewById(R.id.btn_backHome);

        BackHome.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intentCalcu = new Intent(getApplicationContext(), Home.class);
                intentCalcu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentCalcu);
                finish();
            }
        });

        Infrared.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intentInfrared = new Intent(getApplicationContext(), InfraredStudy.class);
                intentInfrared.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentInfrared);
                finish();
            }
        });

        BTTransfer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intentTransfer = new Intent(getApplicationContext(), StudyBtTransfer.class);
                intentTransfer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentTransfer);
                finish();
            }
        });

        BTWireless.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intentWireless = new Intent(getApplicationContext(), StudyBTWireless.class);
                intentWireless.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentWireless);
                finish();
            }
        });
    }


}