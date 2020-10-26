package clement.zentz.go4lunch.ui;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.squareup.picasso.Picasso;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.ui.workmates.WorkmatesAdapter;
import clement.zentz.go4lunch.util.Constants;
import clement.zentz.go4lunch.util.dialogs.PermissionRationaleDialogFragment;
import clement.zentz.go4lunch.util.dialogs.RatingBarDialogFragment;
import clement.zentz.go4lunch.util.notification.AlertReceiver;
import clement.zentz.go4lunch.viewModels.FirestoreViewModel;
import clement.zentz.go4lunch.viewModels.GooglePlacesViewModel;
import clement.zentz.go4lunch.viewModels.SharedViewModel;

public class RestaurantDetailsActivity extends AppCompatActivity implements RatingBarDialogFragment.RatingBarDialogListener {

    private static final String TAG = "RestaurantDetailsActivity";

    //observers
    private Restaurant restaurantWithDetails;

    //recyclerView
    private RecyclerView recyclerView;
    private ImageView restaurantImg;
    private FloatingActionButton fab;
    private WorkmatesAdapter adapter;
    private RatingBar mRatingBar;

    //toolbar
    private Toolbar toolbar;
    private TextView restaurantDetailsName;
    private TextView restaurantDetailsAddress;

    //getIntent
    private String currentUserId;
    private String restaurantId;
    private boolean isYourLunch;

    //viewModels
    private GooglePlacesViewModel mGooglePlacesViewModel;
    private FirestoreViewModel mFirestoreViewModel;
    private SharedViewModel mSharedViewModel;

    private Workmate currentUserFromFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);

        mGooglePlacesViewModel = new ViewModelProvider(this).get(GooglePlacesViewModel.class);
        mFirestoreViewModel = new ViewModelProvider(this).get(FirestoreViewModel.class);
        mSharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        subscribeObservers();

        getIncomingIntent();

        toolbar = findViewById(R.id.restaurant_details_toolbar);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        restaurantDetailsName = findViewById(R.id.restaurant_details_name_txt);
        restaurantDetailsAddress = findViewById(R.id.restaurant_details_address_txt);
        mRatingBar = findViewById(R.id.rating_bar_indicator_details);

        fab = findViewById(R.id.fab);
        restaurantImg = findViewById(R.id.restaurant_detail_img);
        ImageButton restaurantDetailsCallBtn = findViewById(R.id.restaurant_details_call_btn);
        ImageButton restaurantDetailsStarsBtn = findViewById(R.id.restaurant_details_stars_btn);
        ImageButton restaurantDetailsWebsiteBtn = findViewById(R.id.restaurant_details_website_btn);

        recyclerView = findViewById(R.id.restaurantDetail_workmates_rv);
        setupRecyclerView();

        fab.setOnClickListener(view -> {
                currentUserFromFirestore.setRestaurantId(restaurantWithDetails.getPlaceId());
                currentUserFromFirestore.setRestaurantName(restaurantWithDetails.getName());
                currentUserFromFirestore.setRestaurantAddress(restaurantWithDetails.getVicinity());
                currentUserFromFirestore.setTimestamp(Timestamp.now());
                mFirestoreViewModel.addOrUpdateFirestoreCurrentUser(currentUserFromFirestore);
                mFirestoreViewModel.requestAllFirestoreWorkmates();
                startAlarm();
        });

        restaurantDetailsCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(RestaurantDetailsActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    if (restaurantWithDetails.getFormattedPhoneNumber() != null) {
                        callIntent.setData(Uri.parse("tel:+"+restaurantWithDetails.getFormattedPhoneNumber()));
                        startActivity(callIntent);
                    }
                }else if(ActivityCompat.shouldShowRequestPermissionRationale(RestaurantDetailsActivity.this, Manifest.permission.CALL_PHONE)){
                    PermissionRationaleDialogFragment permissionRationaleDialogFragment = new PermissionRationaleDialogFragment();
                    permissionRationaleDialogFragment.show(getSupportFragmentManager() , TAG);
                }else{
                    ActivityCompat.requestPermissions(RestaurantDetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, Constants.CALL_PERMISSION_REQUEST_CODE);
                }
            }
        });

        restaurantDetailsStarsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    RatingBarDialogFragment ratingBarDialogFragment = new RatingBarDialogFragment();
                    ratingBarDialogFragment.show(getSupportFragmentManager(), TAG);
            }
        });

        restaurantDetailsWebsiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (restaurantWithDetails.getWebsite() != null){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(restaurantWithDetails.getWebsite()));
                    startActivity(browserIntent);
                }else {
                    Toast.makeText(RestaurantDetailsActivity.this, "sorry, we could not find any website associate with this place.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        boolean result = super.onSupportNavigateUp();
        finish();
        return result;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void subscribeObservers(){

        mFirestoreViewModel.receiveCurrentUserWithWorkmateId().observe(this, new Observer<Workmate>() {
            @Override
            public void onChanged(Workmate user) {
                if (user != null){
                    currentUserFromFirestore = user;
                    mSharedViewModel.setCurrentUser(user);
                    if (isYourLunch){
                        mGooglePlacesViewModel.restaurantDetails(user.getRestaurantId(), Constants.PLACES_TYPE, 0);
                        mFirestoreViewModel.requestWorkmatesWithRestaurantId(user.getRestaurantId());
                    }
                }
            }
        });

        mGooglePlacesViewModel.getRestaurantDetails().observe(this, new Observer<Restaurant>() {
            @Override
            public void onChanged(Restaurant restaurant) {
                if (restaurant != null){
                    restaurantWithDetails = restaurant;
                    if (restaurantWithDetails.getPhotos() != null){
                        Picasso.get().load(Constants.BASE_URL_PHOTO_PLACE
                                + "key=" + Constants.API_KEY
                                + "&maxwidth=200"
                                + "&maxheight=200"
                                + "&photoreference=" + (restaurantWithDetails.getPhotos().get(0).getPhotoReference()))
                                .into(restaurantImg);
                    }
                    restaurantDetailsName.setText(restaurantWithDetails.getName());
                    restaurantDetailsAddress.setText(restaurantWithDetails.getTypes().get(0)+" - "+restaurantWithDetails.getVicinity());

                    if (restaurantWithDetails.getRating() != null){
                        mRatingBar.setRating((float)((restaurantWithDetails.getRating().floatValue())*(3.0/5.0)));
                    }
                }
            }
        });

        mGooglePlacesViewModel.getRestaurants().observe(this, new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                adapter.setRestaurantList(restaurants);
            }
        });

        mFirestoreViewModel.receiveWorkmatesWithRestaurantId().observe(this, new Observer<List<Workmate>>() {
            @Override
            public void onChanged(List<Workmate> workmateList) {
                adapter.setWorkmateList(workmateList);
            }
        });
    }

    private void setupRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new WorkmatesAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void getIncomingIntent(){
        if (getIntent().hasExtra(Constants.RESTAURANT_DETAILS_CURRENT_USER_ID) && getIntent().hasExtra(Constants.IS_YOUR_LUNCH)){
            currentUserId = getIntent().getStringExtra(Constants.RESTAURANT_DETAILS_CURRENT_USER_ID);
            isYourLunch = getIntent().getBooleanExtra(Constants.IS_YOUR_LUNCH, false);
            mFirestoreViewModel.requestCurrentUserWithId(currentUserId);
            mFirestoreViewModel.requestAllFirestoreWorkmates();
        }
        if (getIntent().hasExtra(Constants.RESTAURANT_DETAILS_CURRENT_RESTAURANT_ID)){
            restaurantId = getIntent().getStringExtra(Constants.RESTAURANT_DETAILS_CURRENT_RESTAURANT_ID);
            mGooglePlacesViewModel.restaurantDetails(restaurantId, Constants.PLACES_TYPE, 0);
            mFirestoreViewModel.requestWorkmatesWithRestaurantId(restaurantId);
        }
    }

    private void startAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra(Constants.RESTAURANT_DETAILS_CURRENT_USER_ID, currentUserId);
        intent.putExtra(Constants.RESTAURANT_DETAILS_CURRENT_RESTAURANT_ID, restaurantId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        // Set the alarm to start at approximately 12:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 15);

        if (alarmManager != null)
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    @Override
    public void onDialogPositiveClick(float rating) {
        mFirestoreViewModel.addOrUpdateRestaurantRating(restaurantWithDetails.getPlaceId(), currentUserId, rating);
    }
}
