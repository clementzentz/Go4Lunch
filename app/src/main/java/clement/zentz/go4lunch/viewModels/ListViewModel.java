package clement.zentz.go4lunch.viewModels;

import android.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import java.util.List;

import clement.zentz.go4lunch.models.placeAutocomplete.Prediction;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.repository.ListRepository;

public class ListViewModel extends ViewModel {

    private static ListViewModel instance;
    private final ListRepository mListRepository;

    private final MediatorLiveData<Pair<List<Restaurant>, String>> allRestaurantsWithNextPageToken = new MediatorLiveData<>();
    private List<Restaurant> allRestaurants;
    private String nextPageToken;
    boolean a, b;

    public static ListViewModel getInstance(){
        if(instance == null){
            instance = new ListViewModel();
        }
        return instance;
    }

    public ListViewModel(){
        mListRepository = ListRepository.getInstance();
        initMediator();
    }

    public void initMediator(){

        allRestaurantsWithNextPageToken.addSource(mListRepository.getRestaurants(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                if (restaurants != null){
                    a = true;
                    allRestaurants = restaurants;
                    updateIfBoth();
                }
            }
        });

        allRestaurantsWithNextPageToken.addSource(mListRepository.getNextPageToken(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null){
                    b = true;
                    nextPageToken = s;
                    updateIfBoth();
                }
            }
        });
    }

    public void updateIfBoth(){
        if (a && b){
            allRestaurantsWithNextPageToken.postValue(Pair.create(allRestaurants, nextPageToken));
        }
    }

    public LiveData<List<Prediction>> getPredictionsPlaceAutocomplete(){
        return mListRepository.getPredictionsPlaceAutocomplete();
    }

    public LiveData<Pair<List<Restaurant>, String>> getRestaurantsWithNextPageToken(){
        return allRestaurantsWithNextPageToken;
    }

    public LiveData<List<Restaurant>> getRestaurants(){
       return mListRepository.getRestaurants();
    }

    public LiveData<Boolean> isRestaurantNearbySearchTimeout(){
        return mListRepository.isRestaurantNearbySearchTimeout();
    }

    public LiveData<List<Workmate>> getWorkmates(){
        return mListRepository.getWorkmates();
    }

    public void searchNearbyRestaurants(String location, String radius, String type, String pageToken){
        mListRepository.searchNearbyRestaurants(location, radius, type, pageToken);
    }

    public void searchPlaceAutocompletePredictions(String userInput, String type, String radius, String location){
        mListRepository.searchPlaceAutocompletePredictions(userInput, type, radius, location);
    }

    public void requestAllWorkmates(){
        mListRepository.requestAllWorkmates();
    }

    public void requestAllGlobalRatings(){
        mListRepository.requestAllGlobalRatings();
    }
}
