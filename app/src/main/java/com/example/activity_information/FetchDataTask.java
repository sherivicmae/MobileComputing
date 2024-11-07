package com.example.activity_information;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.AsyncTask;
import org.json.JSONObject;

public class FetchDataTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
        String result = "";
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            result = stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        // Process the JSON data here
        try {
            JSONObject jsonObject = new JSONObject(result);
            // Access JSON fields here
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
