package clement.zentz.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import clement.zentz.go4lunch.models.placeAutocomplete.Prediction;
import clement.zentz.go4lunch.models.rating.GlobalRating;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.services.firestore.FirestoreApiClient;
import clement.zentz.go4lunch.services.googlePlaces.GooglePlacesAPIClient;

public class ListRepository {

    private static ListRepository instance;
    private final FirestoreApiClient mFirebaseApiClient;
    private final GooglePlacesAPIClient mGooglePlacesAPIClient;

    private final MutableLiveData<Boolean> isQueryExhausted = new MutableLiveData<>();

    // webservices data
    private final MediatorLiveData<List<Restaurant>> allRestaurants = new MediatorLiveData<>();
    private final MutableLiveData<List<Workmate>> allWorkmates = new MutableLiveData<>();
    private List<Workmate> mWorkmateList;
    private List<Restaurant> mRestaurantList;
    private List<GlobalRating> mGlobalRatingList;
    private boolean a, b, c;

    public  static ListRepository getInstance(){
        if(instance == null){
            instance = new ListRepository();
        }
        return instance;
    }

    public ListRepository(){
        mFirebaseApiClient = FirestoreApiClient.getInstance();
        mGooglePlacesAPIClient = GooglePlacesAPIClient.getInstance();
        initMediator();
    }

    //for test
    public ListRepository(FirestoreApiClient firebaseApiClient, GooglePlacesAPIClient googlePlacesAPIClient){
        mFirebaseApiClient = firebaseApiClient;
        mGooglePlacesAPIClient = googlePlacesAPIClient;
    }

    private void initMediator(){

        allRestaurants.addSource(mGooglePlacesAPIClient.getRestaurants(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurantList) {
                if (restaurantList != null){
                    a = true;
                    mRestaurantList = restaurantList;
                    updateIfAll();
                }
            }
        });

        allRestaurants.addSource(mFirebaseApiClient.receiveAllFirestoreWorkmates(), new Observer<List<Workmate>>() {
            @Override
            public void onChanged(List<Workmate> workmates) {
                if (workmates != null){
                    b = true;
                    mWorkmateList = workmates;
                    updateIfAll();
                }
            }
        });

        allRestaurants.addSource(mFirebaseApiClient.receiveAllGlobalRatings(), new Observer<List<GlobalRating>>() {
            @Override
            public void onChanged(List<GlobalRating> globalRatings) {
                if (globalRatings != null){
                    c = true;
                    mGlobalRatingList = globalRatings;
                    updateIfAll();
                }
            }
        });
    }

    private void updateIfAll(){
        if (a && b && c){
            setRestaurantsGlobalRatings();
            setRestaurantsWithWorkmates();
            allRestaurants.postValue(mRestaurantList);
            allWorkmates.postValue(mWorkmateList);
        }
    }

    private void setRestaurantsGlobalRatings(){
        for (Restaurant restaurant : mRestaurantList){
            for (GlobalRating globalRating : mGlobalRatingList){
                if (restaurant.getPlaceId().equals(globalRating.getRestaurantId())){
                    restaurant.setGlobalRating(globalRating.getGlobalRating());
                }
            }
        }
    }

    private void setRestaurantsWithWorkmates(){
        for (Restaurant restaurant : mRestaurantList){
            List<Workmate> workmatesJoining = new ArrayList<>();
            for (Workmate workmate : mWorkmateList){
                if (workmate.getRestaurantId() != null){
                    if (workmate.getRestaurantId().equals(restaurant.getPlaceId())){
                        workmatesJoining.add(workmate);
                        workmate.setRestaurant(restaurant);
                    }
                }
            }
            restaurant.setWorkmatesJoining(workmatesJoining);
        }
    }

    public LiveData<List<Prediction>> getPredictionsPlaceAutocomplete(){
        return mGooglePlacesAPIClient.getPredictionsPlaceAutocomplete();
    }

    public LiveData<List<Restaurant>> getRestaurants(){
        return allRestaurants;
    }

    public LiveData<List<Workmate>> getWorkmates(){
        return allWorkmates;
    }

    public LiveData<String> getNextPageToken(){
        return mGooglePlacesAPIClient.getNextPageToken();
    }

    private void doneQuery(List<Restaurant> list){
        if (list != null){
            if (list.size() % 20 != 0){
                isQueryExhausted.setValue(true);
            }
        }else {
            isQueryExhausted.setValue(true);
        }
    }

    public void searchNearbyRestaurants(String location, String radius, String type, String pageToken){
        mGooglePlacesAPIClient.searchNearbyRestaurants(location, radius, type, pageToken);
    }

    public void searchPlaceAutocompletePredictions(String userInput, String type, String radius, String location){
        mGooglePlacesAPIClient.searchPlaceAutocompletePredictions(userInput, type, radius, location);
    }

    public void requestAllWorkmates(){
        mFirebaseApiClient.requestAllWorkmates();
    }

    public void requestAllGlobalRatings(){
        mFirebaseApiClient.requestAllGlobalRatings();
    }

    public void cancelRequest(){
        mGooglePlacesAPIClient.cancelRequest();
    }
}
