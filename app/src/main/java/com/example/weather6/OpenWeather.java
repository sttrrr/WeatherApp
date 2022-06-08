package com.example.weather6;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.net.ssl.HttpsURLConnection;

public class OpenWeather extends AppCompatActivity {
    private static Map<String, String> nameDesc = new HashMap<String, String>();;
    private TextView textViewCity;
    private TextView textViewTemp;
    private ImageView textViewMainIcon;
    private TextView textViewHundText;
    private TextView textViewRainText;
    private TextView textViewWindText;
    private TextView textViewDescription;
    private TextView textViewMaxTemp;
    private TextView textViewMinTemp;
    private TextView textViewLike;
    private TextView textViewAdvice;
    private String lat;
    private String lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_weather);



        String cityWeather = getIntent().getStringExtra("city");

        textViewMainIcon = findViewById(R.id.weather_icon);
        textViewCity = findViewById(R.id.cityTextBiew);
        textViewTemp = findViewById(R.id.tempWeather);
        textViewCity.setText(cityWeather);
        textViewHundText= findViewById(R.id.humidity_text);
        textViewRainText = findViewById(R.id.pressure_text);
        textViewWindText = findViewById(R.id.wind_text);
        textViewDescription = findViewById(R.id.description);
        textViewMaxTemp = findViewById(R.id.max_text);
        textViewMinTemp = findViewById(R.id.min_text);
        textViewLike = findViewById(R.id.feels_like_text);

        //textViewAdvice = findViewById(R.id.advice_text);
      String url1 = "https://geocode-maps.yandex.ru/1.x/?format=json&apikey=0f9c671e-c7f0-4da2-90f8-e6a9faaaee13&geocode="+cityWeather;
      new Connected().execute(url1);
        String url = "https://api.weather.yandex.ru/v2/forecast?lat="+lat+"&lon="+lon+"&hours=true&limit=5&lang=ru_RU";
        //String url = "https://api.openweathermap.org/data/2.5/weather?q="+ cityWeather+"&appid=4d414a5f570776be9b49ec722a459a33&units=metric&lang=ru";
        //String url = "https://api.openweathermap.org/data/2.5/forecast?q="+ cityWeather+"&appid=4d414a5f570776be9b49ec722a459a33&cnt=3&units=metric";
        new GetURLData().execute(url);
        nameDesc.put("clear", "Ясно");
        nameDesc.put("partly-cloudy ", "Облачно с прояснениями");
        nameDesc.put("cloudy","Облачно");
        nameDesc.put("overcast","Пасмурно");
        nameDesc.put("drizzle","Морось");
        nameDesc.put("light-rain","Небольшой дождь");
        nameDesc.put("rain","Дождь");
        nameDesc.put("moderate-rain","Умеренно сильный дождь");
        nameDesc.put("continuous-heavy-rain","Долгий сильный дождь");
        nameDesc.put("showers","Ливень");
        nameDesc.put("wet-snow","Дождь со снегом");
        nameDesc.put("snow","Снег");
        nameDesc.put("snow-showers","Снегопад");
        nameDesc.put("hail","Град");
        nameDesc.put("thunderstorm","Гроза");
        nameDesc.put("thunderstorm-with-rain","Дождь с грозой");
        nameDesc.put("thunderstorm-with-hail","Гроза с градом");
    }


    public class GetURLData extends AsyncTask<String, String, String> {

        // Будет выполнено до отправки данных по URL
        protected void onPreExecute() {
            super.onPreExecute();
           textViewTemp.setText("Ожидайте...");
        }
        // Будет выполняться во время подключения по URL
        @Override
        public String doInBackground(@NonNull String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                // Создаем URL подключение, а также HTTP подключение
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestProperty("X-Yandex-API-Key","19c1d270-aad8-46f7-b9c7-dc1a7a966085");
                connection.connect();
                connection.setRequestMethod("POST");


                // Создаем объекты для считывания данных из файла
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                // Генерируемая строка
                StringBuilder buffer = new StringBuilder();
                String line = "";

                // Считываем файл и записываем все в строку
                while((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                // Возвращаем строку
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Закрываем соединения
                if(connection != null)
                    connection.disconnect();

                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        // Выполняется после завершения получения данных
        @RequiresApi(api = Build.VERSION_CODES.M)
        @SuppressLint({"SetTextI18n", "CheckResult"})
        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject json = new JSONObject(result);
                JSONObject fact = json.getJSONObject("fact");
                JSONArray forecast = json.getJSONArray("forecasts");
                JSONObject parts = forecast.getJSONObject(0).getJSONObject("parts");
                JSONObject night = parts.getJSONObject("night");
                JSONObject day = parts.getJSONObject("day_short");
                int temp_min = night.getInt("temp_min");
                int temp_max = day.getInt("temp");
                String icon = fact.getString("icon");
                int Temperature= fact.getInt("temp");
                int likeTemp = fact.getInt("feels_like");
                int wind_speed = fact.getInt("wind_speed");
                int preassure = fact.getInt("pressure_mm");
                int hudmunity = fact.getInt("humidity");
                String description = fact.getString("condition");
                textViewDescription.setText(nameDesc.get(description));







           /*     JSONArray array = json.getJSONArray("weather");
                JSONObject object = array.getJSONObject(0);
                JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                String description = object.getString("description");
                String icons = object.getString("icon");
                JSONObject main = json.getJSONObject("main");
//температура

                int Temperature = main.getInt("temp");
                //влажность
                int Humidity = main.getInt("humidity");
//давление
                int Pressure = main.getInt("pressure");
//скорость ветра
                JSONObject wind = json.getJSONObject("wind");
                int windSpeed = wind.getInt("speed");
                //максимальная и минимальная температура
                int maxTemp = main.getInt("temp_max");
                int minTemp = main.getInt("temp_min");
                int likeTemp = main.getInt("feels_like");*/

//выводим все это на экран
                textViewTemp.setText(Temperature + "°C");
                textViewWindText.setText( wind_speed+ " м/с");
                textViewHundText.setText(hudmunity + "%");
                textViewRainText.setText(preassure + " мм.рт.ст");

                textViewLike.setText(likeTemp + "°C");
                textViewMaxTemp.setText(temp_max + "°C");
                textViewMinTemp.setText(temp_min + "°C");
seticon(description,textViewMainIcon);



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void seticon(String num, ImageView icon){
        switch(num)
        {
            case "clear":
                icon.setImageResource(R.drawable.clear);
                break;
            case "partly-cloudy":
                icon.setImageResource(R.drawable.partly_cloudy);
                break;
            case "cloudy":
                icon.setImageResource(R.drawable.cloudy);
                break;
            case "overcast":
                icon.setImageResource(R.drawable.overcast);
                break;
            case "drizzle":
                icon.setImageResource(R.drawable.drizzle);
                break;
            case "light-rain":
                icon.setImageResource(R.drawable.light_rain);
                break;
            case "rain":
                icon.setImageResource(R.drawable.rain);
                break;
            case "moderate-rain":
                icon.setImageResource(R.drawable.rain);
                break;
            case "heavy-rain":
                icon.setImageResource(R.drawable.heavy_rain);
                break;
            case "continuous-heavy-rain":
                icon.setImageResource(R.drawable.continous_heavy_rain);
                break;
            case "showers":
                icon.setImageResource(R.drawable.heavy_rain);
                break;
            case "wet-snow":
                icon.setImageResource(R.drawable.wet_snow);
                break;
            case "light-snow":
                icon.setImageResource(R.drawable.snow);
                break;
            case "snow":
                icon.setImageResource(R.drawable.snow);
                break;
            case "snow-showers":
                icon.setImageResource(R.drawable.snow_showers);
                break;
            case "hail":
                icon.setImageResource(R.drawable.hail);
                break;
            case "thunderstorm":
                icon.setImageResource(R.drawable.thunderstorm);
                break;
            case "thunderstorm-with-rain":
                icon.setImageResource(R.drawable.thunderstorm);
                break;
            case "thunderstorm-with-hail":
                icon.setImageResource(R.drawable.thunderstorm);
                break;
        }

    }


    public void setNameDesc(Map<String, String> nameDesc) {
        this.nameDesc = nameDesc;
    }

     class Connected extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpsURLConnection)url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                // Генерируемая строка
                StringBuilder buffer = new StringBuilder();
                String line = "";

                // Считываем файл и записываем все в строку
                while ((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                // Возвращаем строку
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Закрываем соединения
                if (connection != null)
                    connection.disconnect();

                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;}
        @RequiresApi(api = Build.VERSION_CODES.M)
        @SuppressLint({"SetTextI18n", "CheckResult"})
        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            try {

                JSONObject json1 = new JSONObject(result);


               String coord = json1.getJSONObject("response").getJSONObject("GeoObjectCollection").getJSONArray("featureMember").getJSONObject(0).getJSONObject("boundedBy").getJSONObject("Envelope").getString("lowerCorner");

                String[] parts = coord.split(" ");
                lat = parts[0];
                lon = parts[1];
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}





