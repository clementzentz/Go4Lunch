package clement.zentz.go4lunch.ui.sharedViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.repository.GooglePlaceRepository;

public class SharedViewModel extends ViewModel {

    private GooglePlaceRepository mGooglePlaceRepository;

    public SharedViewModel(){
        mGooglePlaceRepository = GooglePlaceRepository.getInstance();
    }

    public LiveData<List<Restaurant>> getRestaurants(){
        return mGooglePlaceRepository.getRestaurants();
    }

    public void nearbySearchRestaurants(String location, String radius, String type){
        mGooglePlaceRepository.nearbySearchRestaurantsApi(location, radius, type);
    }
}
