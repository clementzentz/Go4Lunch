package clement.zentz.go4lunch;

import android.os.Bundle;
import android.util.Log;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.HashMap;
import java.util.Map;

import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.util.Constants;
import clement.zentz.go4lunch.viewModels.GooglePlacesViewModel;

public class RestaurantDetails extends AppCompatActivity {

    private static final String TAG = "RestaurantDetails";

    private Restaurant currentRestaurant;
    private FloatingActionButton fab;
    private Workmate currentUser;
    private FirebaseFirestore db;

    private GooglePlacesViewModel mGooglePlacesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGooglePlacesViewModel = new  ViewModelProvider(this).get(GooglePlacesViewModel.class);

        setContentView(R.layout.activity_detail_restaurant);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);

        getIncomingIntent();

        mGooglePlacesViewModel.restaurantDetails(currentRestaurant.getPlaceId(), Constants.PLACES_TYPE);

        subscribeRestaurantDetailsObserver();

        setupFirestore();

        fab.setOnClickListener(view -> {
            currentUser.setRestaurantId(currentRestaurant.getPlaceId());
            currentUser.setTimestamp(Timestamp.now());
            addOrUpdateCurrentUserToFirestore();
        });
    }

    private void subscribeRestaurantDetailsObserver(){
        mGooglePlacesViewModel.getRestaurantDetails().observe(this, new Observer<Restaurant>() {
            @Override
            public void onChanged(Restaurant restaurant) {
                currentRestaurant = restaurant;
            }
        });
    }

    private void getIncomingIntent(){
        if (getIntent().hasExtra(Constants.LIST_RESTAURANT_CURRENT_RESTAURANT_ASK_INTENT)){
            currentRestaurant = getIntent().getParcelableExtra(Constants.LIST_RESTAURANT_CURRENT_RESTAURANT_ASK_INTENT);
            currentUser = getIntent().getParcelableExtra(Constants.MAIN_ACTIVITY_CURRENT_USER_ASK_INTENT);
        }
    }

    private void setupFirestore(){
        db = FirebaseFirestore.getInstance();
    }

    private void addOrUpdateCurrentUserToFirestore(){
        // Create a new association between workmate and restaurant
        Map<String, Object> user = new HashMap<>();
        user.put("workmate_id", this.currentUser.getWorkmateId());
        user.put("workmate_name", this.currentUser.getWorkmateName());
        user.put("workmate_email", this.currentUser.getEmail());
        user.put("workmate_photo_url", this.currentUser.getPhotoUrl());
        user.put("restaurant_id", this.currentUser.getRestaurantId());
        user.put("timestamp", this.currentUser.getTimestamp());
        // Add a new document with a generated ID
        db.collection("workmates").document(this.currentUser.getWorkmateId())
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }
}
