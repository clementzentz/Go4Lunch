package clement.zentz.go4lunch.repository;

import androidx.lifecycle.LiveData;

import java.util.List;

import clement.zentz.go4lunch.models.rating.GlobalRating;
import clement.zentz.go4lunch.models.rating.Rating;
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

    //for test
    public FirestoreRepository(FirestoreApi firestoreApi){
        mFirestoreApi = firestoreApi;
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
    
    public LiveData<List<Rating>> receiveAllRatings4ThisRestaurant(){
        return mFirestoreApi.receiveAllRatings4ThisRestaurant();
    }

    public LiveData<GlobalRating> receiveGlobalRating4ThisRestaurant(){
        return mFirestoreApi.receiveGlobalRating4ThisRestaurant();
    }

    public LiveData<List<GlobalRating>> receiveAllGlobalRatings(){
        return mFirestoreApi.receiveAllGlobalRatings();
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

    public void requestAllRatings4ThisRestaurant(String restaurantId){
        mFirestoreApi.requestAllRatings4ThisRestaurant(restaurantId);
    }

    public void requestGlobalRating4ThisRestaurant(String restaurantId){
        mFirestoreApi.requestGlobalRating4ThisRestaurant(restaurantId);
    }

    public void requestAllGlobalRatings(){
        mFirestoreApi.requestAllGlobalRatings();
    }

    public void addOrUpdateFirestoreCurrentUser(Workmate currentUser){
       mFirestoreApi.addOrUpdateFirestoreCurrentUser(currentUser);
    }

    public void addOrUpdateUserRating(Rating rating){
        mFirestoreApi.addOrUpdateUserRating(rating);
    }

    public void addOrUpdateGlobalRating(GlobalRating globalRating){
        mFirestoreApi.addOrUpdateGlobalRating(globalRating);
    }
}
