package clement.zentz.go4lunch.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

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

import java.util.List;

import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.ui.DetailActivity;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.util.dialogs.MapPermissionRationale;
import clement.zentz.go4lunch.viewModels.DetailViewModel;
import clement.zentz.go4lunch.util.Constants;
import clement.zentz.go4lunch.viewModels.ListViewModel;
import clement.zentz.go4lunch.viewModels.SharedViewModel;

public class MapFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MapFragment";

    private SharedViewModel mSharedViewModel;
    private ListViewModel mListViewModel;
    private DetailViewModel mDetailViewModel;

    private String currentUserId;

    //google map
    private GoogleMap map;
    private boolean locationPermissionGranted;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private static final float DEFAULT_ZOOM = 14; //zoom
    private static final LatLng defaultLocation = new LatLng(48.801954,2.334204);

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
        mSharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        mListViewModel = new ViewModelProvider(requireActivity()).get(ListViewModel.class);
        mDetailViewModel = new ViewModelProvider(requireActivity()).get(DetailViewModel.class);
    }

    private void subscribeObservers(){

        mListViewModel.getRestaurants().observe(getViewLifecycleOwner(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                if (map != null) {
                    map.clear();
                    for (Restaurant restaurant : restaurants) {
                        float color;
                        if (!restaurant.getWorkmatesJoining().isEmpty()) {
                            color = BitmapDescriptorFactory.HUE_GREEN;
                        } else {
                            color = BitmapDescriptorFactory.HUE_ORANGE;
                        }
                        map.addMarker(new MarkerOptions()
                                .position(new LatLng(restaurant.getGeometry().getLocation().getLat(), restaurant.getGeometry().getLocation().getLng()))
                                .title(restaurant.getName())
                                .icon(BitmapDescriptorFactory.defaultMarker(color))
                        ).setTag(restaurant.getPlaceId());
                    }
                }
            }
        });

        mDetailViewModel.getRestaurantDetailsPlaceAutocomplete().observe(getViewLifecycleOwner(), new Observer<Restaurant>() {
            @Override
            public void onChanged(Restaurant restaurant) {
                if (map != null && restaurant != null){
                    map.clear();
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(restaurant.getGeometry().getLocation().getLat(),
                                    restaurant.getGeometry().getLocation().getLng()), DEFAULT_ZOOM));
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(restaurant.getGeometry().getLocation().getLat(), restaurant.getGeometry().getLocation().getLng()))
                            .title(restaurant.getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))).setTag(restaurant.getPlaceId());
                }
            }
        });

        mSharedViewModel.getCurrentUserId().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s != null){
                    currentUserId = s;
                }
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        if (marker.getTag() != null){
            intent.putExtra(Constants.INTENT_CURRENT_RESTAURANT_ID, (String) marker.getTag());
            intent.putExtra(Constants.INTENT_CURRENT_USER_ID, currentUserId);
            intent.putExtra(Constants.IS_YOUR_LUNCH, false);
            startActivity(intent);
        }
        return false;
    }

    //google map
    private void configureGoogleMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map_fragment);
        assert mapFragment != null;
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
        if (map != null) {
            map.setOnMyLocationButtonClickListener(this);
            map.setOnMyLocationClickListener(this);
            map.setOnMarkerClickListener(this);
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

            subscribeObservers();
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getContext(), "Current location: " + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
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

                            searchNearbyRestaurants();
                            mSharedViewModel.setLocationUser(lastKnownLocation);
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

    private void searchNearbyRestaurants() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String radius = sharedPreferences.getString("radius", "1000");

        mListViewModel.searchNearbyRestaurants(
                lastKnownLocation.getLatitude() + ", " + lastKnownLocation.getLongitude(),
                radius,
                Constants.PLACES_TYPE, "");
    }

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), this::onActivityResult);

    private void onActivityResult(Boolean isGranted) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            locationPermissionGranted = true;
            getDeviceLocation();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
            MapPermissionRationale mapPermissionDialog = new MapPermissionRationale(requestPermissionLauncher);
            mapPermissionDialog.show(getChildFragmentManager(), TAG);
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }
}
