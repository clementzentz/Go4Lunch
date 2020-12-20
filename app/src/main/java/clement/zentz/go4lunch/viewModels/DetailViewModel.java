package clement.zentz.go4lunch.viewModels;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import clement.zentz.go4lunch.models.rating.GlobalRating;
import clement.zentz.go4lunch.models.rating.Rating;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.repository.DetailRepository;
import clement.zentz.go4lunch.repository.ListRepository;

public class DetailViewModel extends ViewModel {

    private static DetailViewModel instance;
    private final DetailRepository mDetailRepository;

    public static DetailViewModel getInstance(){
        if(instance == null){
            instance = new DetailViewModel();
        }
        return instance;
    }

    public DetailViewModel(){
        mDetailRepository = DetailRepository.getInstance();
    }

    public LiveData<Workmate> getCurrentUser(){
        return mDetailRepository.getCurrentUser();
    }

    public LiveData<Pair<Restaurant, List<Rating>>> getCurrentRestaurant(){
        return mDetailRepository.getCurrentRestaurant();
    }

    public LiveData<Restaurant> getRestaurantDetailsPlaceAutocomplete(){
        return mDetailRepository.getRestaurantDetailPlaceAutocomplete();
    }

    public void requestCurrentUser(String currentUserId){
        mDetailRepository.requestCurrentUser(currentUserId);
    }

    public void searchRestaurantDetails(String restaurantId, String type, int code){
        mDetailRepository.searchRestaurantDetails(restaurantId, type, code);
    }

    public void requestWorkmatesJoining(String restaurantId){
        mDetailRepository.requestWorkmatesJoining(restaurantId);
    }

    public void requestGlobalRating(String restaurantId){
        mDetailRepository.requestGlobalRating(restaurantId);
    }

    public void requestAllRestaurantRatings(String restaurantId){
        mDetailRepository.requestAllRestaurantRatings(restaurantId);
    }

    public void setCurrentUser(Workmate currentUser){
        mDetailRepository.setCurrentUser(currentUser);
    }

    public void setUserRating(Rating rating){
        mDetailRepository.setUserRating(rating);
    }

    public void setGlobalRating(GlobalRating globalRating){
        mDetailRepository.setGlobalRating(globalRating);
    }
}
