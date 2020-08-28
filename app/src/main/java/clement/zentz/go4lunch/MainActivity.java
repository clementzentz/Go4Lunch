package clement.zentz.go4lunch;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.List;

import clement.zentz.go4lunch.models.placeAutocomplete.Prediction;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.viewModels.FirestoreViewModel;
import clement.zentz.go4lunch.viewModels.GooglePlacesViewModel;
import clement.zentz.go4lunch.viewModels.SharedViewModel;
import clement.zentz.go4lunch.util.Constants;

//bottom nav + nav drawer activity
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Workmate currentUser;
    private Location locationUser;

    private SharedViewModel mSharedViewModel;
    private FirestoreViewModel mFirestoreViewModel;
    private GooglePlacesViewModel mGooglePlaceViewModel;

    private Workmate currentUserFromFirestoreRequest;

    //View
    private Toolbar toolbar;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        toolbar = findViewById(R.id.main_activity_toolbar);
        mSearchView = findViewById(R.id.main_activity_search_view);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));

        mSharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        mGooglePlaceViewModel = new ViewModelProvider(this).get(GooglePlacesViewModel.class);
        mFirestoreViewModel = new ViewModelProvider(this).get(FirestoreViewModel.class);

        getIncomingIntentFromAuthActivity();

//        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
//        ListRestaurantFragment listRestaurantFragment = (ListRestaurantFragment) getSupportFragmentManager().findFragmentById(R.id.list_restaurant_fragment);
//        WorkmatesFragment workmatesFragment = (WorkmatesFragment) getSupportFragmentManager().findFragmentById(R.id.list_workmates_fragment);

        BottomNavigationView bottomNavView = findViewById(R.id.bottom_nav_view);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_mapView, R.id.navigation_listRestaurant, R.id.navigation_workmates)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavView, navController);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() >= 3){
                    if (locationUser != null){
                        mGooglePlaceViewModel.placeAutocompleteApi(
                                 s,
                                "establishment",
                                "500",
                                locationUser.getLatitude()+","+locationUser.getLongitude()
                        );
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        subscribeObserver();
        mFirestoreViewModel.requestCurrentUserWithId(currentUser.getWorkmateId());
        mFirestoreViewModel.requestAllFirestoreWorkmates();
    }

    private void subscribeObserver(){

        mSharedViewModel.getLocationUser().observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                locationUser = location;
            }
        });

        mFirestoreViewModel.receiveCurrentUserWithWorkmateId().observe(this, new Observer<Workmate>() {
            @Override
            public void onChanged(Workmate workmate) {
                currentUserFromFirestoreRequest = workmate;
                if (currentUserFromFirestoreRequest != null) {
                    mSharedViewModel.setCurrentUser(currentUserFromFirestoreRequest);
                } else {
                    mFirestoreViewModel.addOrUpdateFirestoreCurrentUser(currentUser);
                }
                mFirestoreViewModel.receiveCurrentUserWithWorkmateId().removeObserver(this);
            }
        });

        mGooglePlaceViewModel.getPredictionsPlaceAutocomplete().observe(this, new Observer<List<Prediction>>() {
            @Override
            public void onChanged(List<Prediction> predictions) {
                if (!predictions.isEmpty()){
                    for (Prediction prediction : predictions){
                        if(prediction.getTypes().contains(Constants.PLACES_TYPE)){
                            mGooglePlaceViewModel.restaurantDetails(prediction.getPlaceId(), Constants.PLACES_TYPE, 1);
                        }
                    }
                }
            }
        });

        mGooglePlaceViewModel.getRestaurantDetails4PlaceAutocomplete().observe(this, new Observer<Restaurant>() {
            @Override
            public void onChanged(Restaurant restaurant) {
                if (restaurant != null){
                    mSharedViewModel.setPlaceAutocompleteRestaurant(restaurant);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.search_button){
            mSearchView.setVisibility(View.VISIBLE);
            item.setVisible(false);
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    //configure nav drawer
    private void configureNavDrawer(){
        //Nav Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View navHeader = navigationView.getHeaderView(0);

        //nav header
        ImageView imageUser = navHeader.findViewById(R.id.user_img);
        TextView nameUser = navHeader.findViewById(R.id.displayName_user_txt);
        TextView emailUser= navHeader.findViewById(R.id.email_user_txt);

        nameUser.setText(currentUser.getWorkmateName());
        emailUser.setText(currentUser.getEmail());
        Picasso.get().load(currentUser.getPhotoUrl()).into(imageUser);

        drawer.addDrawerListener(new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name));

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_drawer_lunch_item :
                    Intent intent = new Intent(this, RestaurantDetails.class);
                        intent.putExtra(Constants.RESTAURANT_DETAILS_CURRENT_USER_INTENT , currentUserFromFirestoreRequest);
                        intent.putExtra(Constants.RESTAURANT_DETAILS_CURRENT_RESTAURANT_ID_INTENT , currentUserFromFirestoreRequest.getRestaurantId());
                        intent.putExtra(Constants.IS_USER_RESTAURANT, true);
                        startActivity(intent);
                    break;
                case R.id.nav_drawer_settings_item :
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                    break;
                case R.id.nav_drawer_logout_item :
                    AuthUI.getInstance()
                            .signOut(this)
                            .addOnCompleteListener(task -> {
                                startActivity(new Intent(this, AuthActivity.class));
                            });
                    break;
            }
            return false;
        });
    }

    private void getIncomingIntentFromAuthActivity(){
        if (getIntent().hasExtra(Constants.AUTH_ACTIVITY_TO_MAIN_ACTIVITY)){
            currentUser = getIntent().getParcelableExtra(Constants.AUTH_ACTIVITY_TO_MAIN_ACTIVITY);
            configureNavDrawer();
        }
    }

    //searchview with listitem dialog
    //ajouter string xml fr + eng
}
