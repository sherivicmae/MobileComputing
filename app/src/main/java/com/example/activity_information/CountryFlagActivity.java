package com.example.activity_information;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountryFlagActivity extends AppCompatActivity {

    HashMap<String, String> countryMap = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_country_flag);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        countryMap.put("PH", "Philippines");
        countryMap.put("US", "United States");
        countryMap.put("CA", "Canada");
        countryMap.put("JP", "Japan");
        countryMap.put("FR", "France");
        countryMap.put("DE", "Germany");
        countryMap.put("IN", "India");
        countryMap.put("CN", "China");
        countryMap.put("BR", "Brazil");
        countryMap.put("AU", "Australia");

        List<Map.Entry<String, String>> countryList = new ArrayList<>(countryMap.entrySet());
        // Set up the adapter
        CountryAdapter adapter = new CountryAdapter(this, countryList);
        ListView listView = findViewById(R.id.lv_countryList); // Make sure listView exists in activity_main.xml
        listView.setAdapter(adapter);
    }
}