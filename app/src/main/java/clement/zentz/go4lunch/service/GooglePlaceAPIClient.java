package clement.zentz.go4lunch.service;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import clement.zentz.go4lunch.AppExecutors;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.service.responses.NearbySearchRestaurantResponse;
import retrofit2.Call;
import retrofit2.Response;

import static clement.zentz.go4lunch.util.Constants.API_KEY;
import static clement.zentz.go4lunch.util.Constants.NETWORK_TIMEOUT;

public class GooglePlaceAPIClient {

    private static final String TAG = "GooglePlaceAPIClient";

    private static GooglePlaceAPIClient instance;
    private MutableLiveData<List<Restaurant>> mRestaurants;
    private RetrieveRestaurantsRunnable mRetrieveRestaurantsRunnable;

    public static GooglePlaceAPIClient getInstance(){
        if (instance == null){
            instance = new GooglePlaceAPIClient();
        }
        return instance;
    }

    private GooglePlaceAPIClient(){
        mRestaurants = new MutableLiveData<>();
    }

    public LiveData<List<Restaurant>> getRestaurants(){
        return  mRestaurants;
    }

    //setup and run runnable
    public void nearbySearchRestaurantApi(String location, String radius, String type){
        if (mRetrieveRestaurantsRunnable != null){
            mRetrieveRestaurantsRunnable = null;
        }
        mRetrieveRestaurantsRunnable = new RetrieveRestaurantsRunnable(location, radius, type);
        final Future handler = AppExecutors.getInstance().getNetworkIO().submit(mRetrieveRestaurantsRunnable);

        AppExecutors.getInstance().getNetworkIO().schedule(() -> {
            //let the user know it's timed out
            handler.cancel(true);
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private class RetrieveRestaurantsRunnable implements Runnable{

        private String location;
        private String radius;
        private String type;
        boolean cancelRequest;

        public RetrieveRestaurantsRunnable(String location, String radius, String type) {
            this.location = location;
            this.radius = radius;
            this.type = type;
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getRestaurants(location, radius, type).execute();
                if (cancelRequest){
                    return;
                }
                if (response.code() == 200){
                    List<Restaurant> list = new ArrayList<>(((NearbySearchRestaurantResponse)response.body()).getRestaurants());
                    mRestaurants.postValue(list);
                }
                else{
                    String error  = response.errorBody().string();
                    Log.e(TAG, "run: "+ error);
                    mRestaurants.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mRestaurants.postValue(null);
            }
        }

        private Call<NearbySearchRestaurantResponse> getRestaurants(String location, String radius, String type){
            return ServiceGenerator.getGooglePlaceApi().nearbySearchRestaurant(API_KEY, location, radius, type);
        }

        private void cancelRequest(){
            Log.d(TAG, "cancelRequest: canceling the search request.");
        }
    }
}
