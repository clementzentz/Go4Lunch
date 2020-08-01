package clement.zentz.go4lunch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.ui.workmates.WorkmatesAdapter;
import clement.zentz.go4lunch.util.Constants;
import clement.zentz.go4lunch.viewModels.FirestoreViewModel;
import clement.zentz.go4lunch.viewModels.GooglePlacesViewModel;

public class RestaurantDetails extends AppCompatActivity {

    private static final String TAG = "RestaurantDetails";

    //observers
    private Restaurant restaurantWithDetails;

    //recyclerView
    private RecyclerView recyclerView;
    private  ImageView restaurantImg;
    private FloatingActionButton fab;
    private WorkmatesAdapter adapter;

    //toolbar
    private Toolbar toolbar;
    private TextView restaurantDetailsName;
    private TextView restaurantDetailsAddress;

    //getIntent
    private Workmate currentUser;
    private String restaurantId;

    //viewModels
    private GooglePlacesViewModel mGooglePlacesViewModel;
    private FirestoreViewModel mFirestoreViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);

        mGooglePlacesViewModel = new ViewModelProvider(this).get(GooglePlacesViewModel.class);
        mFirestoreViewModel = new ViewModelProvider(this).get(FirestoreViewModel.class);

        toolbar = findViewById(R.id.restaurant_details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        restaurantDetailsName = findViewById(R.id.restaurant_details_name_txt);
        restaurantDetailsAddress = findViewById(R.id.restaurant_details_address_txt);

        fab = findViewById(R.id.fab);
        restaurantImg = findViewById(R.id.restaurant_detail_img);
        ImageButton restaurantDetailsCallBtn = findViewById(R.id.restaurant_details_call_btn);
        ImageButton restaurantDetailsStarsBtn = findViewById(R.id.restaurant_details_stars_btn);
        ImageButton restaurantDetailsWebsiteBtn = findViewById(R.id.restaurant_details_website_btn);

        recyclerView = findViewById(R.id.restaurantDetail_workmates_rv);
        setupRecyclerView();

        getIncomingIntent();

        subscribeGooglePlacesObserver();
        subscribeFirestoreObservers();

        mGooglePlacesViewModel.restaurantDetails(restaurantId, Constants.PLACES_TYPE);

        fab.setOnClickListener(view -> {
            currentUser.setRestaurantId(restaurantWithDetails.getPlaceId());
            currentUser.setTimestamp(Timestamp.now());
            mFirestoreViewModel.addOrUpdateFirestoreCurrentUser(currentUser);
            mFirestoreViewModel.requestAllFirestoreWorkmates();
        });

        restaurantDetailsCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                if (restaurantWithDetails.getFormattedPhoneNumber()!= null){
                    callIntent.setData(Uri.parse(restaurantWithDetails.getFormattedPhoneNumber()));
                }
//                startActivity(callIntent);
            }
        });

        restaurantDetailsStarsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        restaurantDetailsWebsiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        boolean result = super.onSupportNavigateUp();
        finish();
        return result;
    }

    private void subscribeGooglePlacesObserver(){
        mGooglePlacesViewModel.getRestaurantDetails().observe(this, new Observer<Restaurant>() {
            @Override
            public void onChanged(Restaurant restaurant) {
                restaurantWithDetails = restaurant;

                if (restaurantWithDetails != null){

                    Picasso.get().load(Constants.BASE_URL_PHOTO_PLACE
                            + "key=" + Constants.API_KEY
                            + "&maxwidth=200"
                            + "&maxheight=200"
                            + "&photoreference=" + (restaurantWithDetails.getPhotos().get(0).getPhotoReference()))
                            .into(restaurantImg);

                    restaurantDetailsName.setText(restaurantWithDetails.getName());
                    restaurantDetailsAddress.setText(restaurantWithDetails.getTypes().get(0)+" - "+restaurantWithDetails.getVicinity());
                }
            }
        });
    }

    private void subscribeFirestoreObservers(){
        mFirestoreViewModel.receiveWorkmatesWithCustomQuery().observe(this, new Observer<List<Workmate>>() {
            @Override
            public void onChanged(List<Workmate> workmateList) {
                List<Workmate> workmatesWithCurrentRestaurantId = new ArrayList<>();
                workmatesWithCurrentRestaurantId.addAll(workmateList);
                if (!workmatesWithCurrentRestaurantId.isEmpty()){
                    adapter.setWorkmateList(workmatesWithCurrentRestaurantId);
                }
            }
        });
    }

    private void setupRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new WorkmatesAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void getIncomingIntent(){
        if (getIntent().hasExtra(Constants.RESTAURANT_DETAILS_CURRENT_RESTAURANT_ID_INTENT) && getIntent().hasExtra(Constants.RESTAURANT_DETAILS_CURRENT_USER_INTENT)){
            restaurantId = getIntent().getStringExtra(Constants.RESTAURANT_DETAILS_CURRENT_RESTAURANT_ID_INTENT);
            currentUser = getIntent().getParcelableExtra(Constants.RESTAURANT_DETAILS_CURRENT_USER_INTENT);

            mFirestoreViewModel.requestWorkmatesWithCustomQuery("restaurant_id", restaurantId);

            if (currentUser.getRestaurantId() == null){
                fab.setVisibility(View.GONE);
                Toast.makeText(this, "pas de restaurant enregistré :(", Toast.LENGTH_LONG).show();
            }
        }
    }
}
