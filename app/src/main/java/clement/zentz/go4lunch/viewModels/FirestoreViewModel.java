package clement.zentz.go4lunch.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.repository.FirestoreRepository;

public class FirestoreViewModel extends ViewModel {

    private FirestoreRepository mFirestoreRepository;

    public FirestoreViewModel(){
        mFirestoreRepository = FirestoreRepository.getInstance();
    }

    public LiveData<List<Workmate>> receiveAllFirestoreWorkmates(){
        return mFirestoreRepository.receiveAllFirestoreWorkmates();
    }

    public LiveData<List<Workmate>> receiveWorkmatesWithRestaurantId(){
        return mFirestoreRepository.receiveWorkmatesWithRestaurantId();
    }

    public LiveData<Workmate> receiveCurrentUserWithWorkmateId(){
        return mFirestoreRepository.receiveCurrentUserWithWorkmateId();
    }

//    public LiveData<FirebaseRestaurantRating> receiveRestaurantRatingWithRestaurantId(){
//        return mFirestoreRepository.receiveRestaurantRatingWithRestaurantId();
//    }

    public void requestAllFirestoreWorkmates(){
        mFirestoreRepository.requestAllFirestoreWorkmates();
    }

    public void requestWorkmatesWithRestaurantId(String restaurantId){
        mFirestoreRepository.requestWorkmatesWithRestaurantId(restaurantId);
    }

    public void requestCurrentUserWithId(String workmateId){
        mFirestoreRepository.requestCurrentUserWithId(workmateId);
    }

    public void addOrUpdateFirestoreCurrentUser(Workmate currentUser){
        mFirestoreRepository.addOrUpdateFirestoreCurrentUser(currentUser);
    }

    public void addOrUpdateRestaurantRating(String restaurantId, String workmateId, float rating){
        mFirestoreRepository.addOrUpdateRestaurantRating(restaurantId, workmateId, rating);
    }
}
