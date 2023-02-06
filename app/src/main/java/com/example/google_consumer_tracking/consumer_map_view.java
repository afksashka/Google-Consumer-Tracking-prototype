package com.example.google_consumer_tracking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class consumer_map_view extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consumer_map_view);
        Toast.makeText(this, "ConsumerMapView", Toast.LENGTH_LONG);
    }
}