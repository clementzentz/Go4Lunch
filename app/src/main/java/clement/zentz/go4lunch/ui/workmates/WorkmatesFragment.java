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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
            addOnceCurrentUserToFirestore();
        });
    }

    private void addOnceCurrentUserToFirestore(){
        // Create a new association between workmate and restaurant
        Map<String, Object> workmate = new HashMap<>();
        workmate.put("workmate_id", currentUser.getWorkmateId());
        workmate.put("workmate_name", currentUser.getWorkmateName());
        workmate.put("workmate_email", currentUser.getEmail());
        workmate.put("workmate_photo_url", currentUser.getPhotoUrl());
        workmate.put("restaurant_id", currentUser.getRestaurantId());
        workmate.put("timestamp", currentUser.getTimestamp());
        // Add a new document with a generated ID
        db.collection("workmates")
                .add(workmate)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
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
