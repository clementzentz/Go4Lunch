package clement.zentz.go4lunch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.viewModels.FirebaseViewModel;
import clement.zentz.go4lunch.util.Constants;

//bottom nav + nav drawer activity
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Workmate currentUser;
    private FirebaseViewModel mFirebaseViewModel;

    private FirebaseFirestore db;
    private List<Workmate> mWorkmates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_nav_activity);

        mFirebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);

        setupFirestore();

        getAllWorkmatesFromFirestore();

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
    }



    //configure nav drawer
    private void configureNavDrawer(){
        //Nav Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View navHeader = navigationView.getHeaderView(0);
        Toolbar toolbar = findViewById(R.id.restaurant_details_toolbar);

        //nav header
        ImageView imageUser = navHeader.findViewById(R.id.user_img);
        TextView nameUser = navHeader.findViewById(R.id.displayName_user_txt);
        TextView emailUser= navHeader.findViewById(R.id.email_user_txt);

        nameUser.setText(currentUser.getWorkmateName());
        emailUser.setText(currentUser.getEmail());
        Picasso.get().load(currentUser.getPhotoUrl()).into(imageUser);

        drawer.addDrawerListener(new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name));

//        setSupportActionBar(toolbar);
//        toolbar.setTitle("User Profile");

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_drawer_lunch_item :
                    startActivity(new Intent(getApplicationContext(), RestaurantDetails.class));
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
            mFirebaseViewModel.setCurrentUser(currentUser);
        }
    }

    private void setupFirestore(){
        db = FirebaseFirestore.getInstance();
    }

    private void getAllWorkmatesFromFirestore(){

        Map<String, Object> currentUserFromFirestore = new HashMap<>();

        db.collection("workmates")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            currentUserFromFirestore.putAll(document.getData());
                            mWorkmates.add(new Workmate(
                                    (String)currentUserFromFirestore.get("workmate_id"),
                                    (String)currentUserFromFirestore.get("workmate_name"),
                                    (String)currentUserFromFirestore.get("workmate_email"),
                                    (String)currentUserFromFirestore.get("workmate_photo_url"),
                                    (String)currentUserFromFirestore.get("restaurant_id"),
                                    (Timestamp)currentUserFromFirestore.get("timestamp")));
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
        mFirebaseViewModel.setWorkmates(mWorkmates);
    }
}
