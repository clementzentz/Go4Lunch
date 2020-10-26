package clement.zentz.go4lunch.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.Timestamp;
import com.squareup.picasso.Picasso;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.util.dialogs.SearchViewListDialogFragment;
import clement.zentz.go4lunch.viewModels.FirestoreViewModel;
import clement.zentz.go4lunch.viewModels.GooglePlacesViewModel;
import clement.zentz.go4lunch.viewModels.SharedViewModel;
import clement.zentz.go4lunch.util.Constants;

import static clement.zentz.go4lunch.util.Constants.CHANNEL_ID;

//bottom nav + nav drawer activity
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Workmate currentUser;
    private Location locationUser;

    private SharedViewModel mSharedViewModel;
    private FirestoreViewModel mFirestoreViewModel;
    private GooglePlacesViewModel mGooglePlaceViewModel;

    //View
    private Toolbar toolbar;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        createNotificationChannel();

        toolbar = findViewById(R.id.main_activity_toolbar);
        mSearchView = findViewById(R.id.main_activity_search_view);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));

        mSharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        mGooglePlaceViewModel = new ViewModelProvider(this).get(GooglePlacesViewModel.class);
        mFirestoreViewModel = new ViewModelProvider(this).get(FirestoreViewModel.class);

        mFirestoreViewModel.requestAllFirestoreWorkmates();

//        getDataFromJsonFile();

        subscribeObservers();

        getIncomingIntentFromAuthActivity();

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

        initSearchView();
    }

    private void initSearchView(){

        SearchViewListDialogFragment searchViewListDialogFragment = new SearchViewListDialogFragment();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() >= 3){
                    searchViewListDialogFragment.show(getSupportFragmentManager(), TAG);
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
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void subscribeObservers(){

        mFirestoreViewModel.receiveAllFirestoreWorkmates().observe(this, new Observer<List<Workmate>>() {
            @Override
            public void onChanged(List<Workmate> workmateList) {
//                if (!workmateList.isEmpty() && currentUser != null){
//                    if (!workmateList.contains(currentUser)){
//                        mFirestoreViewModel.addOrUpdateFirestoreCurrentUser(currentUser);
//                    }
//                }
            }
        });

        mSharedViewModel.getLocationUser().observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                locationUser = location;
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
                    Intent intent = new Intent(this, RestaurantDetailsActivity.class);
                    intent.putExtra(Constants.RESTAURANT_DETAILS_CURRENT_USER_ID, currentUser.getWorkmateId());
                    intent.putExtra(Constants.IS_YOUR_LUNCH, true);
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
            mSharedViewModel.setCurrentUser(currentUser);
            mFirestoreViewModel.requestCurrentUserWithId(currentUser.getWorkmateId());
            configureNavDrawer();
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void getDataFromJsonFile(){
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());

            JSONArray m_jArry = obj.getJSONArray("data");

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject current_jo_inside = m_jArry.getJSONObject(i);

                Workmate workmate = new Workmate(
                        current_jo_inside.getString("workmateId"),
                        current_jo_inside.getString("workmateName"),
                        current_jo_inside.getString("email"),
                        current_jo_inside.getString("photoUrl"),
                        current_jo_inside.getString("restaurantId"),
                        current_jo_inside.getString("restaurantName"),
                        current_jo_inside.getString("restaurantAddress"),
                        Timestamp.now());

                mFirestoreViewModel.addOrUpdateFirestoreCurrentUser(workmate);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {

        String json = null;

        try {
            InputStream is = this.getAssets().open("fake_users.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    //power mockito
}
