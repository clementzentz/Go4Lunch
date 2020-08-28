package clement.zentz.go4lunch.repository;

import androidx.lifecycle.LiveData;

import java.util.List;

import clement.zentz.go4lunch.models.placeAutocomplete.Prediction;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.services.googlePlaces.GooglePlacesAPIClient;

public class GooglePlacesRepository {

    private static GooglePlacesRepository instance;
    private GooglePlacesAPIClient mGooglePlacesAPIClient;

    public static GooglePlacesRepository getInstance(){
        if (instance == null){
            instance = new GooglePlacesRepository();
        }
        return instance;
    }

    private GooglePlacesRepository(){
        mGooglePlacesAPIClient = GooglePlacesAPIClient.getInstance();
    }

    public LiveData<List<Restaurant>> getRestaurants(){
        return mGooglePlacesAPIClient.getRestaurants();
    }

    public LiveData<Restaurant> getRestaurantDetails(){
        return mGooglePlacesAPIClient.getRestaurantDetails();
    }

    public LiveData<Restaurant> getRestaurantDetails4PlaceAutocomplete(){
        return mGooglePlacesAPIClient.getRestaurantDetails4PlaceAutocomplete();
    }

    public LiveData<List<Prediction>> getPredictionsPlaceAutocomplete(){
        return mGooglePlacesAPIClient.getPredictionsPlaceAutocomplete();
    }

    public void nearbySearchRestaurantsApi(String location, String radius, String type){
        mGooglePlacesAPIClient.nearbySearchRestaurantApi(location, radius, type);
    }

    public void restaurantDetailsApi(String restaurantId, String type, int code){
        mGooglePlacesAPIClient.restaurantDetailsApi(restaurantId, type, code);
    }

    public void placeAutocompleteApi(String userInput, String type, String radius, String location){
        mGooglePlacesAPIClient.placeAutoCompleteApi(userInput, type, radius, location);
    }
}
