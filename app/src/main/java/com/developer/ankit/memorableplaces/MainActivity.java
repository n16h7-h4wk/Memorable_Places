package com.developer.ankit.memorableplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView mListView ;
    static ArrayList<String> data = new ArrayList<>();
    static ArrayList<LatLng> location = new ArrayList<>();
    static ArrayAdapter mArrayAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.list_view);
        ArrayList<String> latitudes = new ArrayList<>();
        ArrayList<String> longitudes = new ArrayList<>();
        SharedPreferences prefs = this.getSharedPreferences("com.developer.ankit.memorableplaces", Context.MODE_PRIVATE);
        data.clear();
        latitudes.clear();
        longitudes.clear();
        location.clear();
        try {
            data = (ArrayList<String>)ObjectSerializer.deserialize(prefs.getString("places",ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes = (ArrayList<String>)ObjectSerializer.deserialize(prefs.getString("lat",ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes = (ArrayList<String>)ObjectSerializer.deserialize(prefs.getString("lng",ObjectSerializer.serialize(new ArrayList<String>())));


        } catch (IOException e) {
            e.printStackTrace();
        }

        if(data.size()>0 && latitudes.size()>0 && longitudes.size()>0){
            if(data.size()==latitudes.size() && latitudes.size()==longitudes.size()){
                for(int i =0;i<latitudes.size();i++)
                    location.add(new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble(longitudes.get(i))));
            }
        }else{
            data.add("Add a new place...");
            location.add(new LatLng(0,0));
        }

        mArrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,data);
        mListView.setAdapter(mArrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(getApplicationContext(),MapsActivity.class);
                myIntent.putExtra("placeNumber", position);
                startActivity(myIntent);


            }
        });

    }
}
