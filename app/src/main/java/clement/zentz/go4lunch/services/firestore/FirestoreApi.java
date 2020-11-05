package clement.zentz.go4lunch.services.firestore;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clement.zentz.go4lunch.models.rating.GlobalRating;
import clement.zentz.go4lunch.models.rating.Rating;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.util.Constants;

public class FirestoreApi {

    private static final String TAG = "FirestoreApi";

    private MutableLiveData<List<Workmate>> allWorkmates;
    private MutableLiveData<List<Workmate>> workmatesWithRestaurantId;
    private MutableLiveData<Workmate> currentUserWithWorkmateId;

    private MutableLiveData<List<Rating>> allRatings;
    private MutableLiveData<GlobalRating> globalRating;
    private MutableLiveData<List<GlobalRating>> allGlobalRatings;

    private FirebaseFirestore db;
    private static FirestoreApi instance;

    public FirestoreApi(){
        db = FirebaseFirestore.getInstance();
        allWorkmates = new MutableLiveData<>();
        workmatesWithRestaurantId = new MutableLiveData<>();
        currentUserWithWorkmateId = new MutableLiveData<>();
        allRatings = new MutableLiveData<>();
        globalRating = new MutableLiveData<>();
        allGlobalRatings = new MutableLiveData<>();
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

    public LiveData<List<Rating>> receiveAllRatings4ThisRestaurant(){
        return allRatings;
    }

    public LiveData<GlobalRating> receiveGlobalRating4ThisRestaurant(){
        return globalRating;
    }

    public LiveData<List<GlobalRating>> receiveAllGlobalRatings(){
        return allGlobalRatings;
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

    public void requestAllRatings4ThisRestaurant(String restaurantId){
        List<Rating> ratingList = new ArrayList<>();
        db.collection(Constants.RATINGS_COLLECTION)
                .whereEqualTo(Constants.RESTAURANT_ID, restaurantId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                ratingList.add(convertMapToRating(document.getData()));
                            }
                            allRatings.postValue(ratingList);
                        }else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            allRatings.postValue(null);
                        }
                    }
                });
    }

    public void requestGlobalRating4ThisRestaurant(String restaurantId){

        db.collection(Constants.GLOBAL_RATINGS_COLLECTION)
                .whereEqualTo(Constants.RESTAURANT_ID, restaurantId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()){
                            Log.e(TAG, document.getId() + " => "+document.getData());
                            Log.e(TAG, "onComplete: test2");
                            globalRating.postValue(convertMapToGlobalRating(document.getData()));
                        }
                    }else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        globalRating.postValue(null);
                    }
                });
    }

    public void requestAllGlobalRatings(){

        List<GlobalRating> globalRatingList = new ArrayList<>();

        db.collection(Constants.GLOBAL_RATINGS_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                Log.e(TAG, "onComplete: test");
                                globalRatingList.add(convertMapToGlobalRating(document.getData()));
                            }
                            allGlobalRatings.postValue(globalRatingList);
                        }else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

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

    public void addOrUpdateUserRating(Rating rating){
        Map<String, Object> ratingMap = new HashMap<>();
        ratingMap.put(Constants.RATING, rating.getRating());
        ratingMap.put(Constants.RESTAURANT_ID, rating.getRestaurantId());
        ratingMap.put(Constants.WORKMATE_ID, rating.getWorkmatesId());
        db.collection(Constants.RATINGS_COLLECTION).document(rating.getRestaurantId()+rating.getWorkmatesId())
                .set(ratingMap)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }

    public void addOrUpdateGlobalRating(GlobalRating globalRating){
        Map<String, Object> globalRatingMap = new HashMap<>();
        globalRatingMap.put(Constants.GLOBAL_RATING, globalRating.getGlobalRating());
        globalRatingMap.put(Constants.RESTAURANT_ID, globalRating.getRestaurantId());
        db.collection(Constants.GLOBAL_RATINGS_COLLECTION).document(globalRating.getRestaurantId())
                .set(globalRatingMap)
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

    private Rating convertMapToRating(Map<String, Object> map){
        return new Rating(
                (double) map.get(Constants.RATING),
                (String)map.get(Constants.RESTAURANT_ID),
                (String)map.get(Constants.WORKMATE_ID));
    }

    private GlobalRating convertMapToGlobalRating(Map<String, Object> map){
        return new GlobalRating(
                (double)map.get(Constants.GLOBAL_RATING),
                (String)map.get(Constants.RESTAURANT_ID)
        );
    }
}
