package clement.zentz.go4lunch;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.HashMap;
import java.util.Map;

import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.util.Constants;
import clement.zentz.go4lunch.viewModels.MainActivityViewModel;

public class RestaurantDetails extends AppCompatActivity {

    private static final String TAG = "RestaurantDetails";

    private Restaurant currentRestaurant;
    private FloatingActionButton fab;
    private Workmate currentUser;
    private FirebaseFirestore db;
    private MainActivityViewModel mMainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_restaurant);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);

        getIncomingIntent();

        setupFirestore();

        fab.setOnClickListener(view -> {
            currentUser.setRestaurantId(currentRestaurant.getId());
            addOrUpdateCurrentUserToFirestore();
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

//    private void updateCurrentUserRestaurantIdAndTimestampToFirestore(String restaurantId, Timestamp timestamp){
//        DocumentReference currentUser = db.collection("workmates").document(this.currentUser.getWorkmateId());
//
//        currentUser
//                .update("restaurant_id", restaurantId, "timestamp", timestamp)
//                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
//                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
//    }

    private void getAllWorkmatesFromFirestore(){
        db.collection("workmates")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }
}
