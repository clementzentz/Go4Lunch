package clement.zentz.go4lunch.viewModels;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import clement.zentz.go4lunch.models.workmate.Workmate;

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<Workmate> currentUser = new MutableLiveData<>();
    private MutableLiveData<Location> locationUser = new MutableLiveData<>();

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
}
