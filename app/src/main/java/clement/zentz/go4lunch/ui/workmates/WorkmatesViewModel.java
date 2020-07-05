package clement.zentz.go4lunch.ui.workmates;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import clement.zentz.go4lunch.models.workmate.Workmate;

public class WorkmatesViewModel extends ViewModel {

    private MutableLiveData<Workmate> currentUser = new MutableLiveData<>();

    public LiveData<Workmate> getCurrentUser(){
        return currentUser;
    }

    public void setCurrentUser(Workmate workmate){
        currentUser.setValue(workmate);
    }
}
