package clement.zentz.go4lunch;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
    private List<Workmate> allWorkmates = new ArrayList<>();
    private List<Restaurant> allRestaurants = new ArrayList<>();
    private List<Workmate> currentRestaurantWorkmates = new ArrayList<>();
    private Restaurant restaurantWithDetails;

    //recyclerView
    private RecyclerView recyclerView;
    private  ImageView restaurantImg;
    private FloatingActionButton fab;
    private WorkmatesAdapter adapter;

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

        Toolbar toolbar = findViewById(R.id.restaurant_details_toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        restaurantImg = findViewById(R.id.restaurant_detail_img);

        recyclerView = findViewById(R.id.restaurantDetail_workmates_rv);
        setupRecyclerView();

        getIncomingIntent();

        subscribeGooglePlacesObserver();
        subscribeFirestoreObservers();

        mGooglePlacesViewModel.restaurantDetails(restaurantId, Constants.PLACES_TYPE);

//        toolbar.setTitle(restaurantWithDetails.getName());

        fab.setOnClickListener(view -> {
            currentUser.setRestaurantId(restaurantWithDetails.getPlaceId());
            currentUser.setTimestamp(Timestamp.now());
            mFirestoreViewModel.addOrUpdateFirestoreCurrentUser(currentUser);
            mFirestoreViewModel.requestAllFirestoreWorkmates();
        });
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
                }
            }
        });
    }

    private void subscribeFirestoreObservers(){
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
            if (currentUser.getRestaurantId() == null){
                fab.setVisibility(View.GONE);
                Toast.makeText(this, "pas de restaurant enregistré :(", Toast.LENGTH_LONG).show();
            }
        }
    }
}
