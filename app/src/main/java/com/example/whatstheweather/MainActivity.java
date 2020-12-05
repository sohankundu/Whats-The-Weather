package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.input.InputManager;
import android.icu.text.CaseMap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    Button button;
    TextView resultTextView;
    EditText cityEditText;
    Boolean error;

    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try{

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data != -1){

                    char current = (char) data;
                    result += current;
                    data = reader.read();

                }

                return result;

            } catch(Exception e){
                error = true;
                e.printStackTrace();
                return(null);
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(error){
                Toast.makeText(MainActivity.this, "Invalid City Name!", Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "Please retry with a different city name", Toast.LENGTH_SHORT).show();
            }

            else {

                try {

                    JSONObject jsonObject = new JSONObject(s);

                    String weatherInfo = jsonObject.getString("weather");

                    Log.i("Weather content", weatherInfo);

                    JSONArray arr = new JSONArray(weatherInfo);

                    String result = "";

                    for (int i = 0; i < arr.length(); i++) {

                        JSONObject jsonPart = arr.getJSONObject(i);

                        result += "Weather: " + jsonPart.getString("main") + "\nDescription: " + jsonPart.getString("description");

                    }
                    resultTextView.setText(result);
                    resultTextView.animate().alpha(1).setDuration(2000);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        }
    }

    public void cityEntered(View view) throws UnsupportedEncodingException {

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityEditText.getWindowToken(), 0);

        error = false;
        resultTextView.animate().alpha(0).setDuration(100);

        try {

            String city = URLEncoder.encode(cityEditText.getText().toString(), "UTF-8");

            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=f37343cb14f7172737db88eb8c3f6bec");

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = (EditText) findViewById(R.id.cityEditText);
        button = (Button) findViewById(R.id.button);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        

    }
}
