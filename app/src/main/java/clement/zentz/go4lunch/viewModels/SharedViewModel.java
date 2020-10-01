package clement.zentz.go4lunch.viewModels;

import android.location.Location;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.restaurantsAndWorkmates.RestaurantsAndWorkmates;
import clement.zentz.go4lunch.models.workmate.Workmate;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<Workmate> currentUser = new MutableLiveData<>();
    private MutableLiveData<Location> locationUser = new MutableLiveData<>();
    private MutableLiveData<Restaurant> placeAutocompleteRestaurant = new MutableLiveData<>();

    private MediatorLiveData<RestaurantsAndWorkmates> mRestaurantsAndWorkmatesMediatorLiveData = new MediatorLiveData<>();

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

    public MediatorLiveData<RestaurantsAndWorkmates> getMediatorLiveData(){
        return mRestaurantsAndWorkmatesMediatorLiveData;
    }
}
