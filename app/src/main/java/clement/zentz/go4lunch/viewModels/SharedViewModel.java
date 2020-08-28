package clement.zentz.go4lunch.viewModels;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<Workmate> currentUser = new MutableLiveData<>();
    private MutableLiveData<Location> locationUser = new MutableLiveData<>();
    private MutableLiveData<Restaurant> placeAutocompleteRestaurant = new MutableLiveData<>();

    public LiveData<Workmate> getCurrentUser(){
        return currentUser;
    }

    public LiveData<Location> getLocationUser(){
        return locationUser;
    }

    public LiveData<Restaurant> getPlaceAutocompleteRestaurant(){
        return placeAutocompleteRestaurant;
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
}
