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

    public LiveData<List<Workmate>> receiveWorkmatesWithCustomQuery(){
        return mFirestoreRepository.receiveWorkmatesWithCustomQuery();
    }

    public void requestAllFirestoreWorkmates(){
        mFirestoreRepository.requestAllFirestoreWorkmates();
    }

    public void requestDataWithCustomQuery(String key, String value, String collection){
        mFirestoreRepository.requestDataWithCustomQuery(key, value, collection);
    }

    public void addOrUpdateFirestoreCurrentUser(Workmate currentUser){
        mFirestoreRepository.addOrUpdateFirestoreCurrentUser(currentUser);
    }

    public void addOrUpdateRestaurantRating(String restaurantId, float rating){
        mFirestoreRepository.addOrUpdateRestaurantRating(restaurantId, rating);
    }
}
