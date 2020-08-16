package clement.zentz.go4lunch.repository;

import androidx.lifecycle.LiveData;

import java.util.List;

import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.services.firestore.FirestoreApi;

public class FirestoreRepository {

    private static FirestoreRepository instance;
    private FirestoreApi mFirestoreApi;

    public  static FirestoreRepository getInstance(){
        if(instance == null){
            instance = new FirestoreRepository();
        }
        return instance;
    }

    public FirestoreRepository(){
        mFirestoreApi = FirestoreApi.getInstance();
    }

    public LiveData<List<Workmate>> receiveAllFirestoreWorkmates(){
        return mFirestoreApi.receiveAllFirestoreWorkmates();
    }

    public LiveData<List<Workmate>> receiveWorkmatesWithRestaurantId(){
        return mFirestoreApi.receiveWorkmatesWithRestaurantId();
    }

    public LiveData<Workmate> receiveCurrentUserWithWorkmateId(){
        return mFirestoreApi.receiveCurrentUserWithWorkmateId();
    }

    public void requestAllFirestoreWorkmates() {
        mFirestoreApi.requestAllFirestoreWorkmates();
    }

    public void requestWorkmatesWithRestaurantId(String restaurantId){
        mFirestoreApi.requestWorkmatesWithRestaurantId(restaurantId);
    }

    public void requestCurrentUserWithId(String workmateId){
        mFirestoreApi.requestCurrentUserWithId(workmateId);
    }

    public void addOrUpdateFirestoreCurrentUser(Workmate currentUser){
       mFirestoreApi.addOrUpdateFirestoreCurrentUser(currentUser);
    }

    public void addOrUpdateRestaurantRating(String restaurantId, String workmatesId, float rating){
        mFirestoreApi.addOrUpdateRestaurantRating(restaurantId, workmatesId, rating);
    }
}
