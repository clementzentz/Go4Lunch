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

    public LiveData<List<Workmate>> receiveWorkmatesWithCustomQuery(){
        return mFirestoreApi.receiveWorkmatesWithCustomQuery();
    }
    
    public void requestAllFirestoreWorkmates() {
        mFirestoreApi.requestAllFirestoreWorkmates();
    }

    public void requestDataWithCustomQuery(String key, String value, String collection){
        mFirestoreApi.requestDataWithCustomQuery(key, value, collection);
    }

    public void addOrUpdateFirestoreCurrentUser(Workmate currentUser){
       mFirestoreApi.addOrUpdateFirestoreCurrentUser(currentUser);
    }

    public void addOrUpdateRestaurantRating(String restaurantId, float rating){
        mFirestoreApi.addOrUpdateRestaurantRating(restaurantId, rating);
    }
}
