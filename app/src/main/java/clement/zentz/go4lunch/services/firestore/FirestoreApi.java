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
import clement.zentz.go4lunch.util.convert.ConvertUtil;

public class FirestoreApi {

    private static final String TAG = "FirestoreApi";

    private final MutableLiveData<List<Workmate>> allWorkmates;
    private final MutableLiveData<List<Workmate>> workmatesWithRestaurantId;
    private final MutableLiveData<Workmate> currentUserWithWorkmateId;

    private final MutableLiveData<List<Rating>> allRatings;
    private final MutableLiveData<GlobalRating> globalRating;
    private final MutableLiveData<List<GlobalRating>> allGlobalRatings;

    private final FirebaseFirestore db;
    private static FirestoreApi instance;
    private final ConvertUtil convertUtil;

    public FirestoreApi(){
        convertUtil = new ConvertUtil();
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

    public LiveData<List<Workmate>> receiveAllWorkmates4ThisRestaurant(){
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
                            workmateList.add(convertUtil.convertMapToWorkmate(document.getData()));
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
                            workmateList.add(convertUtil.convertMapToWorkmate(document.getData()));
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
                            workmateList.add(convertUtil.convertMapToWorkmate(document.getData()));
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
                                ratingList.add(convertUtil.convertMapToRating(document.getData()));
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
                            globalRating.postValue(convertUtil.convertMapToGlobalRating(document.getData()));
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
                                globalRatingList.add(convertUtil.convertMapToGlobalRating(document.getData()));
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
        db.collection(Constants.RATINGS_COLLECTION).document(rating.getWorkmatesId().substring(0,14)+rating.getRestaurantId().substring(0,14))
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
}
