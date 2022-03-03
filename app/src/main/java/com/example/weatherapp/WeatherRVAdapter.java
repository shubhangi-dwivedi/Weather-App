package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//extending adapter class and creating ViewHolder
public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;

    public WeatherRVAdapter(Context context, ArrayList<WeatherRVModel> weatherRVModelArrayList) {
        this.context = context;
        this.weatherRVModelArrayList = weatherRVModelArrayList;
    }

    @NonNull
    @Override
    public WeatherRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //initializing layout file and inflate this layout file
        View view= LayoutInflater.from(context).inflate(R.layout.weather_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVAdapter.ViewHolder holder, int position) {
        //setting data to our textviews and imageview

        //creating variable for our model
        WeatherRVModel model= weatherRVModelArrayList.get(position);

        //ImageView
        //as there is no HTTP protocol so we have to pass it inside our application
        //for it, we are concatenating the string
        Picasso.get().load("http://".concat(model.getIcon())).into(holder.conditionIV); //setting image

        //TextView
        holder.temperatureTV.setText(model.getTemperature()+"°c"); //temperature
        holder.windTV.setText(model.getWindSpeed()+"Km/h"); //windspeed

        //time
        SimpleDateFormat input= new SimpleDateFormat("yyyy-MM-dd hh:mm"); //input date format in which we are getting time from our api
        SimpleDateFormat output= new SimpleDateFormat("hh:mm aa"); //our output time format in which we'll display our time & aa represents am/pm
        try{
            //changing our time format
            Date t= input.parse(model.getTime()); //our i/p format
            holder.timeTV.setText(output.format(t));
        }catch (ParseException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherRVModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        //creating variables for our textview which we've created in weather_rv_item (layout)
        //3 textview, 1 imageview
        private TextView windTV, temperatureTV, timeTV;
        private ImageView conditionIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //initializing declared variables with IDs
            windTV= itemView.findViewById(R.id.idTVWindSpeed);
            temperatureTV= itemView.findViewById(R.id.idTVTemperature);
            timeTV= itemView.findViewById(R.id.idTVTime);
            conditionIV= itemView.findViewById(R.id.idTVCondition);
        }
    }


}
