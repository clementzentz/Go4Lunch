package clement.zentz.go4lunch.ui;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.squareup.picasso.Picasso;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.models.rating.GlobalRating;
import clement.zentz.go4lunch.models.rating.Rating;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.ui.workmates.WorkmatesAdapter;
import clement.zentz.go4lunch.util.Constants;
import clement.zentz.go4lunch.util.dialogs.PhoneCallPermissionRationale;
import clement.zentz.go4lunch.util.dialogs.RatingBarDialogFragment;
import clement.zentz.go4lunch.util.notification.AlertReceiver;
import clement.zentz.go4lunch.viewModels.DetailViewModel;

public class DetailActivity extends BaseActivity implements RatingBarDialogFragment.RatingBarDialogListener {

    private static final String TAG = "DetailActivity";

    //adapter
    private WorkmatesAdapter adapter;

    //views
    private RecyclerView recyclerView;
    private ImageView restaurantImg;
    private RatingBar mRatingBar;
    private TextView restaurantDetailsName;
    private TextView restaurantDetailsAddress;
    private FloatingActionButton fab;
    private ImageButton restaurantDetailsCallBtn;
    private ImageButton restaurantDetailsStarsBtn;
    private ImageButton restaurantDetailsWebsiteBtn;

    //values
    private String currentUserId;
    private String currentRestaurantId;
    private Restaurant currentRestaurant;
    private boolean isNotificationEnabled;

    //viewModels
    private DetailViewModel mDetailViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isNotificationEnabled = sharedPreferences.getBoolean("notification", true);

        initViews();
        setupRecyclerView();

        mDetailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);

        subscribeObservers();

        getIncomingIntent();

        //toolbar
        Toolbar toolbar = findViewById(R.id.restaurant_details_toolbar);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        fab.setOnClickListener(view -> {
                mDetailViewModel.updatedCurrentUserField(currentUserId, Constants.RESTAURANT_ID, currentRestaurantId);
                mDetailViewModel.requestWorkmatesJoining(currentRestaurantId);
                if (isNotificationEnabled){
                    startAlarm();
                }
        });

        restaurantDetailsCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    if (currentRestaurant.getFormattedPhoneNumber() != null) {
                        callIntent.setData(Uri.parse("tel:+"+currentRestaurant.getFormattedPhoneNumber()));
                        startActivity(callIntent);
                    }
                }else if(ActivityCompat.shouldShowRequestPermissionRationale(DetailActivity.this, Manifest.permission.CALL_PHONE)){
                    PhoneCallPermissionRationale phoneCallPermissionRationale = new PhoneCallPermissionRationale();
                    phoneCallPermissionRationale.show(getSupportFragmentManager() , TAG);
                }else{
                    ActivityCompat.requestPermissions(DetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, Constants.CALL_PERMISSION_REQUEST_CODE);
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
                if (currentRestaurant.getWebsite() != null){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentRestaurant.getWebsite()));
                    startActivity(browserIntent);
                }else {
                    Toast.makeText(DetailActivity.this, "sorry, we could not find any website associate with this place.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initViews(){
        restaurantDetailsName = findViewById(R.id.restaurant_details_name_txt);
        restaurantDetailsAddress = findViewById(R.id.restaurant_details_address_txt);
        mRatingBar = findViewById(R.id.rating_bar_indicator_details);
        fab = findViewById(R.id.fab);
        restaurantImg = findViewById(R.id.restaurant_detail_img);
        restaurantDetailsCallBtn = findViewById(R.id.restaurant_details_call_btn);
        restaurantDetailsStarsBtn = findViewById(R.id.restaurant_details_stars_btn);
        restaurantDetailsWebsiteBtn = findViewById(R.id.restaurant_details_website_btn);
        recyclerView = findViewById(R.id.restaurantDetail_workmates_rv);
    }

    @Override
    public boolean onSupportNavigateUp() {
        boolean result = super.onSupportNavigateUp();
        finish();
        return result;
    }

    private void setupRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new WorkmatesAdapter(true);
        recyclerView.setAdapter(adapter);
    }


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void subscribeObservers(){

        mDetailViewModel.getCurrentRestaurant().observe(this, new Observer<Pair<Restaurant, List<Rating>>>() {
            @Override
            public void onChanged(Pair<Restaurant, List<Rating>> listPair) {
                if (listPair.first != null){

                    currentRestaurant = listPair.first;

                    if (listPair.second != null){
                        double sum = 0;
                        List<Rating> restaurantRatings = listPair.second;
                        for (Rating rating : restaurantRatings){
                            sum = sum + rating.getRating();
                        }
                        sum = sum / restaurantRatings.size();
                        mDetailViewModel.setGlobalRating(new GlobalRating(sum, currentRestaurant.getPlaceId()));
                    }

                    if (currentRestaurant.getPhotos() != null){
                        Picasso.get().load(Constants.BASE_URL_PHOTO_PLACE
                                + "key=" + Constants.API_KEY
                                + "&maxwidth=200"
                                + "&maxheight=200"
                                + "&photoreference=" + (currentRestaurant.getPhotos().get(0).getPhotoReference()))
                                .resize(200, 200)
                                .centerCrop()
                                .into(restaurantImg);
                    }
                    restaurantDetailsName.setText(currentRestaurant.getName());
                    restaurantDetailsAddress.setText(currentRestaurant.getTypes().get(0)+" - "+ currentRestaurant.getVicinity());

                    if (currentRestaurant.getGlobalRating() != 0){
                        mRatingBar.setRating((float) currentRestaurant.getGlobalRating());
                    }else {
                        mRatingBar.setVisibility(View.GONE);
                    }
                    adapter.setAllWorkmates(currentRestaurant.getWorkmatesJoining());
                }
            }
        });
    }

    private void displayNoRestaurantFound(){
        Picasso.get().load(R.drawable.ic_launcher_background)
                .placeholder(R.drawable.ic_launcher_background)
                .resize(200, 200)
                .centerCrop()
                .into(restaurantImg);

        restaurantDetailsName.setText("No Restaurant Found.");
        restaurantDetailsAddress.setText("Please check network connexion.");
        mRatingBar.setVisibility(View.GONE);
        adapter.setAllWorkmates(new ArrayList<>());
    }

    private void getIncomingIntent(){
        if (getIntent().hasExtra(Constants.INTENT_CURRENT_USER_ID) && getIntent().hasExtra(Constants.INTENT_CURRENT_RESTAURANT_ID)) {
            currentUserId = getIntent().getStringExtra(Constants.INTENT_CURRENT_USER_ID);
            currentRestaurantId = getIntent().getStringExtra(Constants.INTENT_CURRENT_RESTAURANT_ID);
            mDetailViewModel.searchRestaurantDetails(currentRestaurantId, Constants.PLACES_TYPE, 0);
            mDetailViewModel.requestWorkmatesJoining(currentRestaurantId);
            mDetailViewModel.requestGlobalRating(currentRestaurantId);
            mDetailViewModel.requestAllRestaurantRatings(currentRestaurantId);
        }else {
            displayNoRestaurantFound();
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
