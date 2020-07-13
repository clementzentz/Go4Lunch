package clement.zentz.go4lunch.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.repository.GooglePlacesRepository;

public class GooglePlacesViewModel extends ViewModel {

    private GooglePlacesRepository mGooglePlacesRepository;

    public GooglePlacesViewModel(){
        mGooglePlacesRepository = GooglePlacesRepository.getInstance();
    }

    public LiveData<List<Restaurant>> getRestaurants(){
        return mGooglePlacesRepository.getRestaurants();
    }

    public LiveData<Restaurant> getRestaurantDetails(){
        return mGooglePlacesRepository.getRestaurantDetails();
    }

    public void nearbySearchRestaurants(String location, String radius, String type){
        mGooglePlacesRepository.nearbySearchRestaurantsApi(location, radius, type);
    }

    public void restaurantDetails(String placeId, String type){
        mGooglePlacesRepository.restaurantDetailsApi(placeId, type);
    }
}
