package clement.zentz.go4lunch.viewModels;

import android.location.Location;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import clement.zentz.go4lunch.models.rating.GlobalRating;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<Workmate> currentUser = new MutableLiveData<>();
    private final MutableLiveData<Location> locationUser = new MutableLiveData<>();
    private final MutableLiveData<Restaurant> placeAutocompleteRestaurant = new MutableLiveData<>();

    private MutableLiveData<List<Workmate>> allWorkmates = new MutableLiveData<>();
    private MutableLiveData<List<GlobalRating>> allGlobalRatings = new MutableLiveData<>();

    public LiveData<Workmate> getCurrentUser(){
        return currentUser;
    }

    public LiveData<Location> getLocationUser(){
        return locationUser;
    }

    public void setCurrentUser(Workmate workmate){
        currentUser.postValue(workmate);
    }

    public void setLocationUser(Location location){
        locationUser.setValue(location);
    }

    public void setPlaceAutocompleteRestaurant(Restaurant restaurant){
        placeAutocompleteRestaurant.setValue(restaurant);
    }

    public LiveData<List<Workmate>> getAllWorkmates() {
        return allWorkmates;
    }

    public void setAllWorkmates(List<Workmate> workmateList) {
        allWorkmates.setValue(workmateList);
    }

    public LiveData<List<GlobalRating>> getAllGlobalRatings(){
        return allGlobalRatings;
    }

    public void  setAllGlobalRatings(List<GlobalRating> globalRatingList){
        allGlobalRatings.setValue(globalRatingList);
    }
}
