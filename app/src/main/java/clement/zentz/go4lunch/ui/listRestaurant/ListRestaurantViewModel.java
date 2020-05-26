package clement.zentz.go4lunch.ui.listRestaurant;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListRestaurantViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ListRestaurantViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is listRestaurant fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}