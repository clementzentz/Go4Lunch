package clement.zentz.go4lunch.services.firestore;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clement.zentz.go4lunch.models.workmate.Workmate;

public class FirestoreApi {

    private static final String TAG = "FirestoreApi";

    private MutableLiveData<List<Workmate>> allWorkmates;
    private MutableLiveData<List<Workmate>> workmatesWithCustomQuery;

    private FirebaseFirestore db;
    private static FirestoreApi instance;

    public FirestoreApi(){
        db = FirebaseFirestore.getInstance();
        allWorkmates = new MutableLiveData<>();
        workmatesWithCustomQuery = new MutableLiveData<>();
    }

    public static FirestoreApi getInstance(){
        if (instance == null){
            instance = new FirestoreApi();
        }
        return instance;
    }

    public LiveData<List<Workmate>> receiveAllFirestoreWorkmates(){
        return allWorkmates;
    }

    public LiveData<List<Workmate>> receiveWorkmatesWithCustomQuery(){
        return workmatesWithCustomQuery;
    }

    public void requestAllFirestoreWorkmates(){

        List<Workmate> workmateList = new ArrayList<>();

        Map<String, Object> currentFirestoreWorkmate = new HashMap<>();

        db.collection("workmates")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            currentFirestoreWorkmate.putAll(document.getData());
                            workmateList.add(new Workmate(
                                    (String)currentFirestoreWorkmate.get("workmate_id"),
                                    (String)currentFirestoreWorkmate.get("workmate_name"),
                                    (String)currentFirestoreWorkmate.get("workmate_email"),
                                    (String)currentFirestoreWorkmate.get("workmate_photo_url"),
                                    (String)currentFirestoreWorkmate.get("restaurant_id"),
                                    (Timestamp)currentFirestoreWorkmate.get("timestamp")));
                        }
                        allWorkmates.postValue(workmateList);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public void requestWorkmatesWithCustomQuery(String key, String value){

        List<Workmate> workmateList1 = new ArrayList<>();

        Map<String, Object> currentFirestoreWorkmate = new HashMap<>();

        db.collection("workmates")
                .whereEqualTo(key, value)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            currentFirestoreWorkmate.putAll(document.getData());
                            workmateList1.add(new Workmate(
                                    (String)currentFirestoreWorkmate.get("workmate_id"),
                                    (String)currentFirestoreWorkmate.get("workmate_name"),
                                    (String)currentFirestoreWorkmate.get("workmate_email"),
                                    (String)currentFirestoreWorkmate.get("workmate_photo_url"),
                                    (String)currentFirestoreWorkmate.get("restaurant_id"),
                                    (Timestamp)currentFirestoreWorkmate.get("timestamp")));
                        }
                        workmatesWithCustomQuery.postValue(workmateList1);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public void addOrUpdateFirestoreCurrentUser(Workmate currentUser){
        // Create a new association between workmate and restaurant
        Map<String, Object> user = new HashMap<>();
        user.put("workmate_id", currentUser.getWorkmateId());
        user.put("workmate_name", currentUser.getWorkmateName());
        user.put("workmate_email", currentUser.getEmail());
        user.put("workmate_photo_url", currentUser.getPhotoUrl());
        user.put("restaurant_id", currentUser.getRestaurantId());
        user.put("timestamp", currentUser.getTimestamp());
        // Add a new document with a generated ID
        db.collection("workmates").document(currentUser.getWorkmateId())
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }
}
