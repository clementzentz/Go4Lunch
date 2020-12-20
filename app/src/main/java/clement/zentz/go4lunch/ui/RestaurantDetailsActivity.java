package clement.zentz.go4lunch.ui;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
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
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.models.rating.GlobalRating;
import clement.zentz.go4lunch.models.rating.Rating;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.ui.workmates.WorkmatesAdapter;
import clement.zentz.go4lunch.util.Constants;
import clement.zentz.go4lunch.util.dialogs.PermissionRationaleDialogFragment;
import clement.zentz.go4lunch.util.dialogs.RatingBarDialogFragment;
import clement.zentz.go4lunch.util.notification.AlertReceiver;
import clement.zentz.go4lunch.viewModels.DetailViewModel;
import clement.zentz.go4lunch.viewModels.ListViewModel;

public class RestaurantDetailsActivity extends BaseActivity implements RatingBarDialogFragment.RatingBarDialogListener {

    private static final String TAG = "RestaurantDetailsActivity";

    //observers
    private Restaurant restaurantWithDetails;

    //recyclerView
    private RecyclerView recyclerView;
    private ImageView restaurantImg;
    private WorkmatesAdapter adapter;
    private RatingBar mRatingBar;

    private TextView restaurantDetailsName;
    private TextView restaurantDetailsAddress;

    //getIntent
    private String currentUserId;
    private String currentRestaurantId;

    //viewModels
    private DetailViewModel mDetailViewModel;
    private ListViewModel mListViewModel;

    private Workmate currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);

        mDetailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        mListViewModel = new ViewModelProvider(this).get(ListViewModel.class);

        subscribeObservers();

        getIncomingIntent();

        //toolbar
        Toolbar toolbar = findViewById(R.id.restaurant_details_toolbar);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        restaurantDetailsName = findViewById(R.id.restaurant_details_name_txt);
        restaurantDetailsAddress = findViewById(R.id.restaurant_details_address_txt);
        mRatingBar = findViewById(R.id.rating_bar_indicator_details);

        FloatingActionButton fab = findViewById(R.id.fab);
        restaurantImg = findViewById(R.id.restaurant_detail_img);
        ImageButton restaurantDetailsCallBtn = findViewById(R.id.restaurant_details_call_btn);
        ImageButton restaurantDetailsStarsBtn = findViewById(R.id.restaurant_details_stars_btn);
        ImageButton restaurantDetailsWebsiteBtn = findViewById(R.id.restaurant_details_website_btn);

        recyclerView = findViewById(R.id.restaurantDetail_workmates_rv);
        setupRecyclerView();

        fab.setOnClickListener(view -> {
                currentUser.setRestaurantId(restaurantWithDetails.getPlaceId());
                currentUser.setTimestamp(Timestamp.now());
                mDetailViewModel.setCurrentUser(currentUser);
                mListViewModel.requestAllWorkmates();
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

    private void setupRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new WorkmatesAdapter();
        recyclerView.setAdapter(adapter);
    }


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void subscribeObservers(){

//        mFirestoreViewModel.receiveCurrentUserWithWorkmateId().observe(this, new Observer<Workmate>() {
//            @Override
//            public void onChanged(Workmate user) {
//                if (user != null){
//                    currentUserFromFirestore = user;
//                    mSharedViewModel.setCurrentUserId(user);
//                    if (isYourLunch){
//                        mGooglePlacesViewModel.restaurantDetails(user.getRestaurantId(), Constants.PLACES_TYPE, 0);
//                        mFirestoreViewModel.requestWorkmatesWithRestaurantId(user.getRestaurantId());
//                    }
//                }
//            }
//        });
//
//        mGooglePlacesViewModel.getRestaurantDetails().observe(this, new Observer<Restaurant>() {
//            @Override
//            public void onChanged(Restaurant restaurant) {
//                if (restaurant != null){
//                    restaurantWithDetails = restaurant;
//                    if (restaurantWithDetails.getPhotos() != null){
//                        Picasso.get().load(Constants.BASE_URL_PHOTO_PLACE
//                                + "key=" + Constants.API_KEY
//                                + "&maxwidth=200"
//                                + "&maxheight=200"
//                                + "&photoreference=" + (restaurantWithDetails.getPhotos().get(0).getPhotoReference()))
//                                .resize(200, 200)
//                                .centerCrop()
//                                .into(restaurantImg);
//                    }
//                    restaurantDetailsName.setText(restaurantWithDetails.getName());
//                    restaurantDetailsAddress.setText(restaurantWithDetails.getTypes().get(0)+" - "+restaurantWithDetails.getVicinity());
//                }
//            }
//        });
//
//        mGooglePlacesViewModel.getRestaurants().observe(this, new Observer<List<Restaurant>>() {
//            @Override
//            public void onChanged(List<Restaurant> restaurants) {
//                adapter.setRestaurantList(restaurants);
//            }
//        });
//
//        mFirestoreViewModel.receiveAllWorkmates4ThisRestaurant().observe(this, new Observer<List<Workmate>>() {
//            @Override
//            public void onChanged(List<Workmate> workmateList) {
//                adapter.setAllWorkmates(workmateList);
//            }
//        });
//
//        mFirestoreViewModel.receiveAllRatings4ThisRestaurant().observe(this, new Observer<List<Rating>>() {
//            @Override
//            public void onChanged(List<Rating> ratings) {
//                double ratingsSum = 0;
//                if (!ratings.isEmpty() && ratings != null){
//                    for (Rating rating : ratings){
//                        ratingsSum = ratingsSum + rating.getRating();
//                    }
//                    GlobalRating globalRating = new GlobalRating((float)ratingsSum/ratings.size(), restaurantId);
//                    mFirestoreViewModel.addOrUpdateGlobalRating(globalRating);
//                    mFirestoreViewModel.requestAllGlobalRatings();
//                }
//            }
//        });
//
//        mFirestoreViewModel.receiveGlobalRating4ThisRestaurant().observe(this, new Observer<GlobalRating>() {
//            @Override
//            public void onChanged(GlobalRating globalRating) {
//                if (globalRating != null){
//                    mRatingBar.setRating((float) globalRating.getGlobalRating());
//                }else {
//                    mRatingBar.setVisibility(View.GONE);
//                }
//            }
//        });

        mDetailViewModel.getCurrentRestaurant().observe(this, new Observer<Pair<Restaurant, List<Rating>>>() {
            @Override
            public void onChanged(Pair<Restaurant, List<Rating>> listPair) {
                if (listPair.first != null){

                    Restaurant restaurant = listPair.first;

                    if (listPair.second != null){
                        double sum = 0;
                        List<Rating> restaurantRatings = listPair.second;
                        for (Rating rating : restaurantRatings){
                            sum = sum + rating.getRating();
                        }
                        mDetailViewModel.setGlobalRating(new GlobalRating(sum, restaurant.getPlaceId()));

                    }

                    if (restaurant.getPhotos() != null){
                        Picasso.get().load(Constants.BASE_URL_PHOTO_PLACE
                                + "key=" + Constants.API_KEY
                                + "&maxwidth=200"
                                + "&maxheight=200"
                                + "&photoreference=" + (restaurant.getPhotos().get(0).getPhotoReference()))
                                .resize(200, 200)
                                .centerCrop()
                                .into(restaurantImg);
                    }
                    restaurantDetailsName.setText(restaurant.getName());
                    restaurantDetailsAddress.setText(restaurant.getTypes().get(0)+" - "+restaurant.getVicinity());

                    if (restaurant.getGlobalRating() != 0){
                        mRatingBar.setRating((float) restaurant.getGlobalRating());
                    }else {
                        mRatingBar.setVisibility(View.GONE);
                    }
                    adapter.setAllWorkmates(restaurant.getWorkmatesJoining());
                }
            }
        });
    }

    private void displayNoRestaurantFound(){
        Picasso.get().load("")
                .placeholder(R.drawable.ic_launcher_background)
                .resize(200, 200)
                .centerCrop()
                .into(restaurantImg);

        restaurantDetailsName.setText("No Restaurant Found.");
        restaurantDetailsAddress.setText("Please check network connexion.");
        mRatingBar.setVisibility(View.GONE);
        adapter.setAllWorkmates(null);
    }

    private void getIncomingIntent(){
        if (getIntent().hasExtra(Constants.INTENT_CURRENT_USER_ID) && getIntent().hasExtra(Constants.INTENT_CURRENT_RESTAURANT_ID)){
            currentUserId = getIntent().getStringExtra(Constants.INTENT_CURRENT_USER_ID);
            currentRestaurantId = getIntent().getStringExtra(Constants.INTENT_CURRENT_RESTAURANT_ID);
            if (currentRestaurantId.equals("")){
                displayNoRestaurantFound();
            }else {
                mDetailViewModel.searchRestaurantDetails(currentRestaurantId, Constants.PLACES_TYPE, 0);
                mDetailViewModel.requestWorkmatesJoining(currentRestaurantId);
            }
        }
    }

    private void startAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra(Constants.INTENT_CURRENT_USER_ID, currentUserId);
        intent.putExtra(Constants.INTENT_CURRENT_RESTAURANT_ID, currentRestaurantId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        // Set the alarm to start at approximately 12:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);

        if (alarmManager != null)
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    @Override
    public void onDialogPositiveClick(float rating) {
        Rating userRating = new Rating(rating, currentRestaurantId, currentUserId);
        mDetailViewModel.setUserRating(userRating);
        mDetailViewModel.requestAllRestaurantRatings(currentRestaurantId);
        mDetailViewModel.requestGlobalRating(currentRestaurantId);
    }
}
