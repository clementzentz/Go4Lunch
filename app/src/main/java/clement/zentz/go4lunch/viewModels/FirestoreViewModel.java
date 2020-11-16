package clement.zentz.go4lunch.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import clement.zentz.go4lunch.models.rating.GlobalRating;
import clement.zentz.go4lunch.models.rating.Rating;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.repository.FirestoreRepository;

public class FirestoreViewModel extends ViewModel {

    private FirestoreRepository mFirestoreRepository;

    public FirestoreViewModel(){
        mFirestoreRepository = FirestoreRepository.getInstance();
    }

    //for test
    public FirestoreViewModel(FirestoreRepository firestoreRepository){
        mFirestoreRepository = firestoreRepository;
    }

    public LiveData<List<Workmate>> receiveAllFirestoreWorkmates(){
        return mFirestoreRepository.receiveAllFirestoreWorkmates();
    }

    public LiveData<List<Workmate>> receiveAllWorkmates4ThisRestaurant(){
        return mFirestoreRepository.receiveAllWorkmates4ThisRestaurant();
    }

    public LiveData<Workmate> receiveCurrentUserWithWorkmateId(){
        return mFirestoreRepository.receiveCurrentUserWithWorkmateId();
    }

    public LiveData<List<Rating>> receiveAllRatings4ThisRestaurant(){
        return mFirestoreRepository.receiveAllRatings4ThisRestaurant();
    }

    public LiveData<GlobalRating> receiveGlobalRating4ThisRestaurant(){
        return mFirestoreRepository.receiveGlobalRating4ThisRestaurant();
    }

    public LiveData<List<GlobalRating>> receiveAllGlobalRatings(){
        return mFirestoreRepository.receiveAllGlobalRatings();
    }

    public void requestAllFirestoreWorkmates(){
        mFirestoreRepository.requestAllFirestoreWorkmates();
    }

    public void requestWorkmatesWithRestaurantId(String restaurantId){
        mFirestoreRepository.requestWorkmatesWithRestaurantId(restaurantId);
    }

    public void requestCurrentUserWithId(String workmateId){
        mFirestoreRepository.requestCurrentUserWithId(workmateId);
    }

    public void requestAllRatings4ThisRestaurant(String restaurantId){
        mFirestoreRepository.requestAllRatings4ThisRestaurant(restaurantId);
    }

    public void requestGlobalRating4ThisRestaurant(String restaurantId){
        mFirestoreRepository.requestGlobalRating4ThisRestaurant(restaurantId);
    }

    public void requestAllGlobalRatings(){
        mFirestoreRepository.requestAllGlobalRatings();
    }

    public void addOrUpdateFirestoreCurrentUser(Workmate currentUser){
        mFirestoreRepository.addOrUpdateFirestoreCurrentUser(currentUser);
    }

    public void addOrUpdateUserRating(Rating rating){
        mFirestoreRepository.addOrUpdateUserRating(rating);
    }

    public void addOrUpdateGlobalRating(GlobalRating globalRating){
        mFirestoreRepository.addOrUpdateGlobalRating(globalRating);
    }
}
