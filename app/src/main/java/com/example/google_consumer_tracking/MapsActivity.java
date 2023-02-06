package com.example.google_consumer_tracking;

import static java.util.Objects.requireNonNull;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.google_consumer_tracking.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.mapsplatform.transportation.consumer.ConsumerApi;
import com.google.android.libraries.mapsplatform.transportation.consumer.managers.TripModelManager;
import com.google.android.libraries.mapsplatform.transportation.consumer.view.ConsumerController;
import com.google.android.libraries.mapsplatform.transportation.consumer.view.ConsumerGoogleMap;
import com.google.android.libraries.mapsplatform.transportation.consumer.view.ConsumerMapView;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private JsonAuthTokenFactory authTokenFactory;
    @Nullable
    private LatLng lastLocation;

    private static final LatLng DEFAULT_MAP_LOCATION = new LatLng(37.423061, -122.084051);
    @MonotonicNonNull
    private ConsumerGoogleMap googleMap;
    private static final int DEFAULT_ZOOM = 16;
    private ConsumerMapView consumerMapView;
    private ConsumerController consumerController;
    private static final String TAG = "MapsActivity";
    private ConsumerApi consumerApi;
    @MonotonicNonNull private TripModelManager tripModelManager;
    private ConsumerGoogleMap consumerGoogleMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        initializeSdk();
        Log.i(TAG, "Consumer SDK version: " + ConsumerApi.getConsumerSDKVersion());
        Toast.makeText(this, "Consumer SDK version: " + ConsumerApi.getConsumerSDKVersion(), Toast.LENGTH_LONG).show();


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    private void initializeSdk() {

        consumerMapView.getConsumerGoogleMapAsync(
                new ConsumerGoogleMap.ConsumerMapReadyCallback() {
                    @Override
                    public void onConsumerMapReady(ConsumerGoogleMap consumerMap) {
                        // Safe to do so as controller will only be nullified during consumerMap's onDestroy()
                        consumerGoogleMap = consumerMap;
                        consumerController = consumerMap.getConsumerController();
                        Task<ConsumerApi> consumerApiTask =
                                ConsumerApi.initialize(
                                        MapsActivity.this,
                                        "xpress-366609",
                                        new JsonAuthTokenFactory());
                        consumerApiTask.addOnSuccessListener(
                                consumerApi ->
                                        tripModelManager = requireNonNull(consumerApi.getTripModelManager()));
                        consumerApiTask.addOnFailureListener(
                                task -> Log.e(TAG, "ConsumerApi Initialization Error:\n" + task.getMessage()));
//                        ConsumerMarkerUtils.setCustomMarkers(consumerController, SampleAppActivity.this);
//                        setupViewBindings();
                        googleMap = consumerGoogleMap;
//                        centerCameraToLastLocation();
//                        setupMapListener();
                    }
                },
                /* fragmentActivity= */ this,
                /* googleMapOptions= */ null);
    }



}