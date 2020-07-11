package clement.zentz.go4lunch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.viewModels.MainActivityViewModel;
import clement.zentz.go4lunch.util.Constants;

//bottom nav + nav drawer activity
public class MainActivity extends AppCompatActivity {

    private Workmate currentUser;
    private MainActivityViewModel mMainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_nav_activity);

        mMainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

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
        Toolbar toolbar = findViewById(R.id.toolbar);

        //nav header
        ImageView imageUser = navHeader.findViewById(R.id.user_img);
        TextView nameUser = navHeader.findViewById(R.id.displayName_user_txt);
        TextView emailUser= navHeader.findViewById(R.id.email_user_txt);

        nameUser.setText(currentUser.getWorkmateName());
        emailUser.setText(currentUser.getEmail());

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
            mMainActivityViewModel.setCurrentUser(currentUser);
        }
    }
}
