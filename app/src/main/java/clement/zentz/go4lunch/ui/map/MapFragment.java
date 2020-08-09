package clement.zentz.go4lunch.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.RestaurantDetails;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.viewModels.FirestoreViewModel;
import clement.zentz.go4lunch.viewModels.GooglePlacesViewModel;
import clement.zentz.go4lunch.util.Constants;

public class MapFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MapFragment";

    private GooglePlacesViewModel mGooglePlacesViewModel;
    private FirestoreViewModel mFirestoreViewModel;

    private List<Restaurant> mRestaurantList = new ArrayList<>();
    private List<Workmate> mWorkmateList = new ArrayList<>();

    private List<Workmate> workmatesListFromCustomQuery = new ArrayList<>();
    private Workmate currentUser;

    //google map
    private GoogleMap map;
    private boolean locationPermissionGranted;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private static final float DEFAULT_ZOOM = 14; //zoom
    private static final LatLng defaultLocation = new LatLng(48.801659, 2.334064);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //setup location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        View root = inflater.inflate(R.layout.fragment_map_restaurant, container, false);

        configureGoogleMap();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mGooglePlacesViewModel = new ViewModelProvider(requireActivity()).get(GooglePlacesViewModel.class);
        mFirestoreViewModel = new ViewModelProvider(requireActivity()).get(FirestoreViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirestoreViewModel.requestAllFirestoreWorkmates();
    }

    private void subscribeGooglePlacesObserver(){
        mGooglePlacesViewModel.getRestaurants().observe(getViewLifecycleOwner(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                mRestaurantList = restaurants;
                addMarkersOnMap();
            }
        });
    }

    private void subscribeFirestoreObservers(){
        mFirestoreViewModel.receiveAllFirestoreWorkmates().observe(getViewLifecycleOwner(), workmates -> {
            mWorkmateList = workmates;
            addMarkersOnMap();
        });

        mFirestoreViewModel.receiveWorkmatesWithCustomQuery().observe(getViewLifecycleOwner(), workmateList -> {
            workmatesListFromCustomQuery = workmateList;
            if (!workmatesListFromCustomQuery.isEmpty()){
                currentUser = workmatesListFromCustomQuery.get(0);
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(getContext(), RestaurantDetails.class);
        String markerTitle = marker.getTitle();
        for (Restaurant restaurant: mRestaurantList){
            if (restaurant.getName().equals(markerTitle)){
                intent.putExtra(Constants.RESTAURANT_DETAILS_CURRENT_RESTAURANT_ID_INTENT, restaurant.getPlaceId());
                intent.putExtra(Constants.RESTAURANT_DETAILS_CURRENT_USER_INTENT, currentUser);
                startActivity(intent);
            }
        }
        return false;
    }

    private void addMarkersOnMap(){
        if (!mRestaurantList.isEmpty() && !mWorkmateList.isEmpty()){
            for (Restaurant restaurant : mRestaurantList) {
                for (Workmate workmate : mWorkmateList){
                    if (!workmate.getRestaurantId().equals(restaurant.getPlaceId())){
                        map.addMarker(new MarkerOptions()
                                .position(new LatLng(restaurant.getGeometry().getLocation().getLat(), restaurant.getGeometry().getLocation().getLng()))
                                .title(restaurant.getName())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    }else{
                        map.addMarker(new MarkerOptions()
                                .position(new LatLng(restaurant.getGeometry().getLocation().getLat(), restaurant.getGeometry().getLocation().getLng()))
                                .title(restaurant.getName())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    }
                }
            }
        }
    }

    //google map
    private void configureGoogleMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);

        map.setOnMarkerClickListener(this);

        updateLocationUI();

        getLastKnownLocation();

        getDeviceLocation();

        subscribeGooglePlacesObserver();

        subscribeFirestoreObservers();
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getContext(), "Current location: " + location, Toast.LENGTH_LONG).show();
        getDeviceLocation();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        getDeviceLocation();
        return false;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);

            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                            nearbySearchRestaurantsApi();
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        map.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                        map.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void nearbySearchRestaurantsApi() {
        mGooglePlacesViewModel.nearbySearchRestaurants(
                lastKnownLocation.getLatitude() + ", " + lastKnownLocation.getLongitude(),
                String.valueOf(Constants.RADIUS),
                Constants.PLACES_TYPE);
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }else {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            lastKnownLocation = location;
                        }
                    });
        }
    }
}
