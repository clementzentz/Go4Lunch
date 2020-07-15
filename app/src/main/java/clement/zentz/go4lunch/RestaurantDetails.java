package clement.zentz.go4lunch;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

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

    private List<Workmate> allWorkmates;
    private List<Workmate> currentRestaurantWorkmates = new ArrayList<>();
    private Restaurant currentRestaurant;

    private RecyclerView recyclerView;
    private WorkmatesAdapter adapter;

    private Workmate currentUser;

    private GooglePlacesViewModel mGooglePlacesViewModel;
    private FirestoreViewModel mFirestoreViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGooglePlacesViewModel = new ViewModelProvider(this).get(GooglePlacesViewModel.class);
        mFirestoreViewModel = new ViewModelProvider(this).get(FirestoreViewModel.class);

        setContentView(R.layout.activity_detail_restaurant);
        Toolbar toolbar = findViewById(R.id.restaurant_details_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);

        recyclerView = findViewById(R.id.restaurantDetail_workmates_rv);
        setupRecyclerView();

        getIncomingIntent();

        toolbar.setTitle(currentRestaurant.getName());

        mGooglePlacesViewModel.restaurantDetails(currentRestaurant.getPlaceId(), Constants.PLACES_TYPE);

        subscribeGooglePlacesObserver();

        fab.setOnClickListener(view -> {
            currentUser.setRestaurantId(currentRestaurant.getPlaceId());
            currentUser.setTimestamp(Timestamp.now());
            mFirestoreViewModel.addOrUpdateFirestoreCurrentUser(currentUser);
            adapter.notifyDataSetChanged();
        });
    }

    private void subscribeGooglePlacesObserver(){
        mGooglePlacesViewModel.getRestaurantDetails().observe(this, new Observer<Restaurant>() {
            @Override
            public void onChanged(Restaurant restaurant) {
                currentRestaurant = restaurant;
                for (Workmate workmate : allWorkmates){
                    if (workmate.getRestaurantId().equals(currentRestaurant.getPlaceId())){
                        currentRestaurantWorkmates.add(workmate);
                    }
                }
                adapter.setWorkmateList(currentRestaurantWorkmates);
            }
        });
    }

    private void setupRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new WorkmatesAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void getIncomingIntent(){
        if (getIntent().hasExtra(Constants.LIST_RESTAURANT_CURRENT_RESTAURANT_ASK_INTENT)){
            currentRestaurant = getIntent().getParcelableExtra(Constants.LIST_RESTAURANT_CURRENT_RESTAURANT_ASK_INTENT);
            currentUser = getIntent().getParcelableExtra(Constants.MAIN_ACTIVITY_CURRENT_USER_ASK_INTENT);
            allWorkmates = getIntent().getParcelableArrayListExtra(Constants.ALL_WORKMATES_INTENT);
        }
    }
}
