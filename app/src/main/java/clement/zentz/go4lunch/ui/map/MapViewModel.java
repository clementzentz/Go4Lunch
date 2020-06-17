package clement.zentz.go4lunch.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import clement.zentz.go4lunch.models.restaurant.Restaurant;

public class MapViewModel extends ViewModel {

    private MutableLiveData<List<Restaurant>> mListMutableLiveData = new MutableLiveData<>();

    public LiveData<List<Restaurant>> getListMutableLiveData() {
        return mListMutableLiveData;
    }

    public void  setListMutableLiveData(List<Restaurant> listLiveData) {
        mListMutableLiveData.setValue(listLiveData);
    }
}