package clement.zentz.go4lunch.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.util.dialogs.SearchViewListDialogFragment;
import clement.zentz.go4lunch.viewModels.DetailViewModel;
import clement.zentz.go4lunch.viewModels.ListViewModel;
import clement.zentz.go4lunch.viewModels.SharedViewModel;
import clement.zentz.go4lunch.util.Constants;

import static clement.zentz.go4lunch.util.Constants.CHANNEL_ID;

//bottom nav + nav drawer activity
public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private Location locationUser;

    private SharedViewModel mSharedViewModel;
    private DetailViewModel mDetailViewModel;
    private ListViewModel mListViewModel;

    //Appbar Views
    private Toolbar toolbar;
    private SearchView mSearchView;

    //navDrawer Views
    private ImageView currentUserImage;
    private TextView currentUserName;
    private TextView currentUserEmail;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    //bottom nav
    BottomNavigationView bottomNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mSharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        mDetailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        mListViewModel = new ViewModelProvider(this).get(ListViewModel.class);

        subscribeObservers();

        initViews();

        createNotificationChannel();

        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));

        getIncomingIntentFromAuthActivity();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_mapView, R.id.navigation_listRestaurant, R.id.navigation_workmates)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavView, navController);

        setupSearchView();
        showProgressBar(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mListViewModel.requestAllWorkmates();
        mListViewModel.requestAllGlobalRatings();
    }

    private void initViews(){
        //Nav Drawer
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View navHeader = navigationView.getHeaderView(0);
        currentUserImage = navHeader.findViewById(R.id.user_img);
        currentUserName = navHeader.findViewById(R.id.displayName_user_txt);
        currentUserEmail = navHeader.findViewById(R.id.email_user_txt);

        //appbar
        toolbar = findViewById(R.id.main_activity_toolbar);
        mSearchView = findViewById(R.id.main_activity_search_view);

        //bottom nav
        bottomNavView = findViewById(R.id.bottom_nav_view);
    }

    private void setupSearchView(){

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
                        mListViewModel.searchPlaceAutocompletePredictions(
                                s,
                                "establishment",
                                "1000",
                                locationUser.getLatitude()+","+locationUser.getLongitude()
                        );
                    }
                }
                return false;
            }
        });
    }

    private void subscribeObservers(){

        mSharedViewModel.getLocationUser().observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                if (location != null){
                    locationUser = location;
                }
            }
        });

        mDetailViewModel.getCurrentUser().observe(this, new Observer<Workmate>() {
            @Override
            public void onChanged(Workmate workmate) {
                if (workmate != null){
                    configureNavDrawer(workmate);
                }
            }
        });

        mListViewModel.isRestaurantNearbySearchTimeout().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                   Toast.makeText(getApplicationContext(), "sorry we could not find the restaurants, please check network connexion.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

//    private void displayErrorScreen(String errorMessage){
//        currentUserName.setText("Error retrieving user...");
//        currentUserEmail.setText("");
//
//        Picasso.get().load("")
//                .placeholder(R.drawable.ic_launcher_background)
//                .into(currentUserImage);
//
////      showParent();
//        showProgressBar(false);
//    }

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
            mSearchView.setVisibility(View.GONE);
            return super.onOptionsItemSelected(item);
        }
    }

    private void getIncomingIntentFromAuthActivity(){
        if (getIntent().hasExtra(Constants.AUTH_ACTIVITY_TO_MAIN_ACTIVITY)){
            String currentUserId = getIntent().getStringExtra(Constants.AUTH_ACTIVITY_TO_MAIN_ACTIVITY);
            mSharedViewModel.setCurrentUserId(currentUserId);
            mDetailViewModel.requestCurrentUser(currentUserId);
        }
    }

    //configure nav drawer
    private void configureNavDrawer(Workmate currentUser){

        currentUserName.setText(currentUser.getWorkmateName());
        currentUserEmail.setText(currentUser.getEmail());
        Picasso.get().load(currentUser.getPhotoUrl()).into(currentUserImage);

        drawer.addDrawerListener(new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name));

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_drawer_lunch_item :
                    Intent intent = new Intent(this, RestaurantDetailsActivity.class);
                    intent.putExtra(Constants.INTENT_CURRENT_USER_ID, currentUser.getWorkmateId());
                    intent.putExtra(Constants.INTENT_CURRENT_RESTAURANT_ID, currentUser.getRestaurantId());
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

        showProgressBar(false);
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
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    //power mockito
}
