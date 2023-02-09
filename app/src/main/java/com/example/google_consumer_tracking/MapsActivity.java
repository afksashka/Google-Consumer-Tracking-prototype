package com.example.google_consumer_tracking;

import static java.util.Objects.requireNonNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.google_consumer_tracking.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.mapsplatform.transportation.consumer.ConsumerApi;
import com.google.android.libraries.mapsplatform.transportation.consumer.managers.TripModel;
import com.google.android.libraries.mapsplatform.transportation.consumer.managers.TripModelCallback;
import com.google.android.libraries.mapsplatform.transportation.consumer.managers.TripModelManager;
import com.google.android.libraries.mapsplatform.transportation.consumer.model.TripInfo;
import com.google.android.libraries.mapsplatform.transportation.consumer.model.TripModelOptions;
import com.google.android.libraries.mapsplatform.transportation.consumer.sessions.JourneySharingSession;
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
    private JourneySharingSession session;
    private Button actionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        consumerMapView = binding.consumerMapView;
        actionButton = findViewById(R.id.buttonCreateTrip);
        actionButton.setOnClickListener(this::onActionButtonClicked);

        initializeSdk();
        getComsumerApi();
        initConsumerController();
        Log.i(TAG, "Consumer SDK version: " + ConsumerApi.getConsumerSDKVersion());
        Toast.makeText(this, "Consumer SDK version: " + ConsumerApi.getConsumerSDKVersion(), Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (session != null) {
            session.stop();
        }
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
                    }
                },
                /* fragmentActivity= */ this,
                /* googleMapOptions= */ null);
    }

    private void getComsumerApi(){
        Task<ConsumerApi> consumerApiTask = ConsumerApi.initialize(
                this, "xpress-366609", new JsonAuthTokenFactory());

        consumerApiTask.addOnSuccessListener(
                consumerApiSdk -> this.consumerApi = consumerApiSdk);
        consumerApiTask.addOnFailureListener(
                task -> Log.e(TAG, "ConsumerApi Initialization Error:\n" + task.getMessage()));
    }

    private void initConsumerController(){
        consumerMapView.getConsumerGoogleMapAsync(
                new ConsumerGoogleMap.ConsumerMapReadyCallback() {
                    @Override
                    public void onConsumerMapReady(@NonNull ConsumerGoogleMap consumerMap) {
                        consumerGoogleMap = consumerMap;
                        consumerController = consumerMap.getConsumerController();
                    }
                },
                this, null);
    }

    private void createTrip(){
        String tripName = "TestDrive";
        tripModelManager = consumerApi.getTripModelManager();
        TripModel tripModel = tripModelManager.getTripModel(tripName);

// Create a JourneySharingSession instance based on the TripModel.
        session = JourneySharingSession.createInstance(tripModel);

// Add the JourneySharingSession instance on the map for updating the UI.
        consumerController.showSession(session);
        consumerController.setAutoCameraEnabled(true);
// Register for trip update events.
        tripModel.registerTripCallback(new TripModelCallback() {
            @Override
            public void onTripETAToNextWaypointUpdated(
                    TripInfo tripInfo, @Nullable Long timestampMillis) {
                // ...
            }

            @Override
            public void onTripActiveRouteRemainingDistanceUpdated(
                    TripInfo tripInfo, @Nullable Integer distanceMeters) {
                // ...
            }

            // ...
        });

        // Set refresh interval to 2 seconds.
        TripModelOptions tripOptions =
                TripModelOptions.builder().setRefreshIntervalMillis(2000).build();
        tripModel.setTripModelOptions(tripOptions);
    }
    private void onActionButtonClicked(View view) {
        Toast.makeText(this, "But Click", Toast.LENGTH_LONG).show();
        createTrip();
    }


}