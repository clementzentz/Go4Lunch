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

    private final MutableLiveData<String> currentUserId = new MutableLiveData<>();
    private final MutableLiveData<Location> locationUser = new MutableLiveData<>();

    public LiveData<String> getCurrentUserId(){
        return currentUserId;
    }

    public LiveData<Location> getLocationUser(){
        return locationUser;
    }

    public void setCurrentUserId(String userId){
        currentUserId.postValue(userId);
    }

    public void setLocationUser(Location location){
        locationUser.setValue(location);
    }
}
