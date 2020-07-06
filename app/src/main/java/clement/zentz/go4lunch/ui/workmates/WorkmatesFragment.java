package clement.zentz.go4lunch.ui.workmates;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;

public class WorkmatesFragment extends Fragment{

    private static final String TAG = "WorkmatesFragment";

    private RecyclerView recyclerView;
    private Restaurant mRestaurant;
    private Workmate currentUser;
    private FirebaseFirestore db;

    private WorkmatesViewModel mWorkmatesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_workmates, container, false);

        setupFirestore();

        mWorkmatesViewModel = ViewModelProviders.of(getActivity()).get(WorkmatesViewModel.class);
        subscribeObserver();

        recyclerView = root.findViewById(R.id.workmates_rv);
        setupRecyclerView();

        return root;
    }

    private void setupRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        WorkmatesAdapter adapter = new WorkmatesAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void setupFirestore(){
        db = FirebaseFirestore.getInstance();
    }

    private void subscribeObserver(){
        mWorkmatesViewModel.getCurrentUser().observe(getViewLifecycleOwner(), workmate -> {
            currentUser = workmate;
            Log.d(TAG, "subscribeObserver: "+currentUser.toString());
            addAndUpdateCurrentUserToFirestore();
        });
    }

    private void addAndUpdateCurrentUserToFirestore(){
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

    private void updateCurrentUserRestaurantIdAndTimestampToFirestore(String restaurantId, Timestamp timestamp){
        DocumentReference currentUser = db.collection("workmates").document(this.currentUser.getWorkmateId());

        currentUser
                .update("restaurant_id", restaurantId, "timestamp", timestamp)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }

    private void getAllWorkmatesFromFirestore(){
        db.collection("workmates")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
