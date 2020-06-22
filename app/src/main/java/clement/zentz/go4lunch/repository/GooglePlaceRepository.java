package clement.zentz.go4lunch.repository;

import androidx.lifecycle.LiveData;

import java.util.List;

import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.service.GooglePlaceAPIClient;

public class GooglePlaceRepository {

    private  static GooglePlaceRepository instance;
    private GooglePlaceAPIClient mGooglePlaceAPIClient;

    public static GooglePlaceRepository getInstance(){
        if (instance == null){
            instance = new GooglePlaceRepository();
        }
        return instance;
    }

    private GooglePlaceRepository(){
        mGooglePlaceAPIClient = GooglePlaceAPIClient.getInstance();
    }

    public LiveData<List<Restaurant>> getRestaurants(){
        return mGooglePlaceAPIClient.getRestaurants();
    }

    public void nearbySearchRestaurantsApi(String location, String radius, String type){
        mGooglePlaceAPIClient.nearbySearchRestaurantApi(location, radius, type);
    }
}
