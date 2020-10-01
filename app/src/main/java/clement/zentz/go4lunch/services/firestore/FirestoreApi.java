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
import clement.zentz.go4lunch.util.Constants;

public class FirestoreApi {

    private static final String TAG = "FirestoreApi";

    private MutableLiveData<List<Workmate>> allWorkmates;
    private MutableLiveData<List<Workmate>> workmatesWithRestaurantId;
    private MutableLiveData<Workmate> currentUserWithWorkmateId;

    private FirebaseFirestore db;
    private static FirestoreApi instance;

    public FirestoreApi(){
        db = FirebaseFirestore.getInstance();
        allWorkmates = new MutableLiveData<>();
        workmatesWithRestaurantId = new MutableLiveData<>();
        currentUserWithWorkmateId = new MutableLiveData<>();
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

    public LiveData<List<Workmate>> receiveWorkmatesWithRestaurantId(){
        return workmatesWithRestaurantId;
    }

    public LiveData<Workmate> receiveCurrentUserWithWorkmateId(){
        return currentUserWithWorkmateId;
    }

    public void requestAllFirestoreWorkmates(){

        List<Workmate> workmateList = new ArrayList<>();

        db.collection(Constants.WORKMATES_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            workmateList.add(convertMapToWorkmate(document.getData()));
                        }
                        allWorkmates.postValue(workmateList);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public void requestWorkmatesWithRestaurantId(String restaurantId){

        List<Workmate> workmateList = new ArrayList<>();

        db.collection(Constants.WORKMATES_COLLECTION)
                .whereEqualTo(Constants.RESTAURANT_ID, restaurantId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            workmateList.add(convertMapToWorkmate(document.getData()));
                        }
                        workmatesWithRestaurantId.postValue(workmateList);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public void requestCurrentUserWithId(String workmateId){

        List<Workmate> workmateList = new ArrayList<>();

        db.collection(Constants.WORKMATES_COLLECTION)
                .whereEqualTo(Constants.WORKMATE_ID, workmateId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            workmateList.add(convertMapToWorkmate(document.getData()));
                        }if (!workmateList.isEmpty()){
                            currentUserWithWorkmateId.postValue(workmateList.get(0));
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

//    public void requestRestaurantRatingWithRestaurantId(String restaurantId){
//
//        List<FirebaseRestaurantRating> firebaseRestaurantRatings = new ArrayList<>();
//
//        db.collection(Constants.RATINGS_COLLECTION)
//                .whereEqualTo(Constants.RESTAURANT_ID, restaurantId)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()){
//                        for (QueryDocumentSnapshot document : task.getResult()){
//                            Log.d(TAG, document.getId() + " => "+document.getData());
//                            firebaseRestaurantRatings.add(convertMapToFirebaseRestaurantRating(document.getData()));
//                        }
//                        if (!firebaseRestaurantRatings.isEmpty()){
//                            restaurantRating.postValue(firebaseRestaurantRatings.get(0));
//                        }
//                    }else {
//                        Log.d(TAG, "Error getting documents: ", task.getException());
//                        restaurantRating.postValue(null);
//                    }
//                });
//    }

    public void addOrUpdateFirestoreCurrentUser(Workmate currentUser){
        // Create a new association between workmate and restaurant
        Map<String, Object> user = new HashMap<>();
        user.put(Constants.WORKMATE_ID, currentUser.getWorkmateId());
        user.put(Constants.WORKMATE_NAME, currentUser.getWorkmateName());
        user.put(Constants.WORKMATE_EMAIL, currentUser.getEmail());
        user.put(Constants.WORKMATE_PHOTO_URL, currentUser.getPhotoUrl());
        user.put(Constants.RESTAURANT_ID, currentUser.getRestaurantId());
        user.put(Constants.RESTAURANT_NAME, currentUser.getRestaurantName());
        user.put(Constants.RESTAURANT_ADDRESS, currentUser.getRestaurantAddress());
        user.put(Constants.TIMESTAMP, currentUser.getTimestamp());
        // Add a new document with a generated ID
        db.collection(Constants.WORKMATES_COLLECTION).document(currentUser.getWorkmateId())
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }

    public void addOrUpdateRestaurantRating(String restaurantId, String workmateId, float rating){
        Map<String, Object> ratingData = new HashMap<>();
        ratingData.put(Constants.RESTAURANT_ID, restaurantId);
        ratingData.put(Constants.WORKMATE_ID, workmateId);
        ratingData.put(Constants.RESTAURANT_RATING, rating);
        db.collection(Constants.RATINGS_COLLECTION).document(restaurantId)
                .set(ratingData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }

    private Workmate convertMapToWorkmate (Map<String, Object> map){
        return new Workmate(
                (String)map.get(Constants.WORKMATE_ID),
                (String)map.get(Constants.WORKMATE_NAME),
                (String)map.get(Constants.WORKMATE_EMAIL),
                (String)map.get(Constants.WORKMATE_PHOTO_URL),
                (String)map.get(Constants.RESTAURANT_ID),
                (String)map.get(Constants.RESTAURANT_NAME),
                (String)map.get(Constants.RESTAURANT_ADDRESS),
                (Timestamp)map.get(Constants.TIMESTAMP));
    }

//    private FirebaseRestaurantRating convertMapToFirebaseRestaurantRating(Map<String, Object> map){
//        return new FirebaseRestaurantRating(
//                (String)map.get(Constants.RESTAURANT_ID),
//                (float)map.get(Constants.RESTAURANT_RATING));
//    }
}
