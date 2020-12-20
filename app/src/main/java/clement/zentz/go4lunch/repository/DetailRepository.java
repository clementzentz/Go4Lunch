package clement.zentz.go4lunch.repository;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import java.util.List;

import clement.zentz.go4lunch.models.rating.GlobalRating;
import clement.zentz.go4lunch.models.rating.Rating;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.services.firestore.FirestoreApiClient;
import clement.zentz.go4lunch.services.googlePlaces.GooglePlacesAPIClient;

public class DetailRepository {

    private final FirestoreApiClient mFirebaseApiClient;
    private final GooglePlacesAPIClient mGooglePlacesAPIClient;

    private final MediatorLiveData<Pair<Restaurant, List<Rating>>> currentRestaurant = new MediatorLiveData<>();
    private Restaurant mCurrentRestaurant;
    private List<Rating> mRatings;
    private GlobalRating mGlobalRating;
    private List<Workmate> mWorkmatesJoining;
    private boolean hasGlobalRating, hasRestaurant, hasWorkmatesJoining, hasRatings;

    private static DetailRepository instance;

    public  static DetailRepository getInstance(){
        if(instance == null){
            instance = new DetailRepository();
        }
        return instance;
    }

    public DetailRepository(){
        mFirebaseApiClient = FirestoreApiClient.getInstance();
        mGooglePlacesAPIClient = GooglePlacesAPIClient.getInstance();
        initCurrentUserMediator();
    }

    private void initCurrentUserMediator(){

        currentRestaurant.addSource(mGooglePlacesAPIClient.getRestaurantDetails(), new Observer<Restaurant>() {
            @Override
            public void onChanged(Restaurant restaurant) {
                if (restaurant != null){
                    hasRestaurant = true;
                    mCurrentRestaurant = restaurant;
                    updateIfAll();
                }
            }
        });

        currentRestaurant.addSource(mFirebaseApiClient.receiveGlobalRating(), new Observer<GlobalRating>() {
            @Override
            public void onChanged(GlobalRating globalRating) {
                if (globalRating != null){
                    hasGlobalRating = true;
                    mGlobalRating = globalRating;
                    updateIfAll();
                }
            }
        });

        currentRestaurant.addSource(mFirebaseApiClient.receiveWorkmatesJoining(), new Observer<List<Workmate>>() {
            @Override
            public void onChanged(List<Workmate> workmates) {
                if (workmates != null){
                    hasWorkmatesJoining = true;
                    mWorkmatesJoining = workmates;
                    updateIfAll();
                }
            }
        });

        currentRestaurant.addSource(mFirebaseApiClient.receiveAllRestaurantRatings(), new Observer<List<Rating>>() {
            @Override
            public void onChanged(List<Rating> ratings) {
                if (ratings != null){
                    hasRatings = true;
                    mRatings = ratings;
                    updateIfAll();
                }
            }
        });

    }

    private void updateIfAll(){
        if (hasRestaurant && hasGlobalRating && hasWorkmatesJoining && hasRatings){
            mCurrentRestaurant.setWorkmatesJoining(mWorkmatesJoining);
            mCurrentRestaurant.setGlobalRating(mGlobalRating.getGlobalRating());
            currentRestaurant.postValue(Pair.create(mCurrentRestaurant, mRatings));
        }
    }

    public LiveData<Workmate> getCurrentUser(){
        return mFirebaseApiClient.receiveCurrentUser();
    }

    public LiveData<Pair<Restaurant, List<Rating>>> getCurrentRestaurant(){
        return currentRestaurant;
    }

    public LiveData<Restaurant> getRestaurantDetailPlaceAutocomplete(){
        return mGooglePlacesAPIClient.getRestaurantDetails4PlaceAutocomplete();
    }

    public void requestCurrentUser(String currentUserId){
        mFirebaseApiClient.requestCurrentUser(currentUserId);
    }

    public void searchRestaurantDetails(String restaurantId, String type, int code){
        mGooglePlacesAPIClient.searchRestaurantDetails(restaurantId, type, code);
    }

    public void requestWorkmatesJoining(String restaurantId){
        mFirebaseApiClient.requestWorkmatesJoining(restaurantId);
    }

    public void requestGlobalRating(String restaurantId){
        mFirebaseApiClient.requestGlobalRating(restaurantId);
    }

    public void requestAllRestaurantRatings(String restaurantId){
        mFirebaseApiClient.requestAllRestaurantRatings(restaurantId);
    }

    public void setCurrentUser(Workmate currentUser){
        mFirebaseApiClient.setCurrentUser(currentUser);
    }

    public void setUserRating(Rating rating){
        mFirebaseApiClient.setUserRating(rating);
    }

    public void setGlobalRating(GlobalRating globalRating){
        mFirebaseApiClient.setGlobalRating(globalRating);
    }
}
