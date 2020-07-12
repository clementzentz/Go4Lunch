package clement.zentz.go4lunch.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import clement.zentz.go4lunch.models.workmate.Workmate;

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<Workmate> currentUser = new MutableLiveData<>();

    private MutableLiveData<List<Workmate>> mWorkmates = new MutableLiveData<>();

    public LiveData<Workmate> getCurrentUser(){
        return currentUser;
    }

    public LiveData<List<Workmate>> getWorkmates(){
        return mWorkmates;
    }

    public void setCurrentUser(Workmate workmate){
        currentUser.setValue(workmate);
    }

    public void setWorkmates(List<Workmate> workmates){
        mWorkmates.setValue(workmates);
    }
}
