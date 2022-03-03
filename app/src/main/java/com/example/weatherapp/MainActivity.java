package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //declaring variables
    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV, temperatureTV, conditionTV;
    private RecyclerView weatherRV;
    private TextInputEditText cityEdt;
    private ImageView backIV, iconIV, searchIV;

    //for adapter class
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;
    private WeatherRVAdapter weatherRVAdapter;

    //for user locations we have to check the permissions
    private LocationManager locationManager;
    private int PERMISSION_CODE=1; //will use it when we're granting the permissions

    //variable for cityName
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //adding flags for displaying our application in full screen so that we don't see the status bar inside application
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);

        //initializing ids to variables
        homeRL = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idPBLoading);
        cityNameTV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        weatherRV = findViewById(R.id.idRVWeather);
        cityEdt = findViewById(R.id.idEdtCity);
        backIV = findViewById(R.id.idIVBack);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idIVSearch);

        weatherRVModelArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this,weatherRVModelArrayList);
        weatherRV.setAdapter(weatherRVAdapter);//setting adapter to recyclerview


        //for user
        //getting user's current location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //also added permissions in manifests for using the permission, and permission for using internet

        //checking whether user have granted permissions or not
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            //if permissions not granted
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

        //if permissions granted
        Location location= locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


        //storing city name
        cityName = getCityName(location.getLongitude(), location.getLatitude());    //this method will provide us the city name

        getWeatherInfo(cityName);

        //adding search icon
        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEdt.getText().toString();

                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter city Name", Toast.LENGTH_SHORT).show();
                }else{
                    cityNameTV.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });

    }

    //when the permission is granted by the user.. checking permissions


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Please Grant Permissions", Toast.LENGTH_SHORT).show();
                finish();   //if user doesn't provide permission.. then close the app
            }
        }
    }

    //for passing longitude & latitude for getting City name
    private String getCityName(double longitude, double latitude){
        String cityName = "Not found"; //when city is not found using latitide & longitude

        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try{
            List<Address> addresses =  gcd.getFromLocation(latitude, longitude, 10);    //getting address from the location
            for(Address adr : addresses){
                if(adr!=null){
                    String city = adr.getLocality(); //this method will return city from longitude & latitude
                    if(city!=null && !city.equals("")){
                        cityName = city;
                    }else{
                        Log.d("TAG", "City NOT FOUND"); //print in our LOGCAT
                        Toast.makeText(this,"USER CITY NOT FOUND", Toast.LENGTH_SHORT).show(); //displaying toast msg
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return cityName;
    }


    //method for getting weather info.
    private void getWeatherInfo(String cityName){
        //prasing data from API
        String url = "http://api.weatherapi.com/v1/forecast.json?key=fbdb577441a744578a5162146221102&q="+cityName+"&days=1&aqi=yes&alerts=yes";
        //currently using this url for JSON parsing

        //setting city name to our city textview
        cityNameTV.setText(cityName);

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
    }
}