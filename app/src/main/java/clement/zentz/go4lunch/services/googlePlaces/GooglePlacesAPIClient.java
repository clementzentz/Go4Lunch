package clement.zentz.go4lunch.services.googlePlaces;

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
import clement.zentz.go4lunch.services.googlePlaces.responses.NearbySearchRestaurantResponse;
import clement.zentz.go4lunch.services.googlePlaces.responses.RestaurantDetailsResponse;
import retrofit2.Call;
import retrofit2.Response;

import static clement.zentz.go4lunch.util.Constants.API_KEY;
import static clement.zentz.go4lunch.util.Constants.NETWORK_TIMEOUT;

public class GooglePlacesAPIClient {

    private static final String TAG = "GooglePlacesAPIClient";

    private static GooglePlacesAPIClient instance;

    //nearbySearchRequest
    private MutableLiveData<List<Restaurant>> mRestaurants;
    private RetrieveNearbyRestaurantsRunnable mRetrieveNearbyRestaurantsRunnable;

    //placeDetailsRequest
    private MutableLiveData<Restaurant> mRestaurantDetails;
    private RetrieveRestaurantDetailsRunnable mRetrieveRestaurantDetailsRunnable;

    public static GooglePlacesAPIClient getInstance(){
        if (instance == null){
            instance = new GooglePlacesAPIClient();
        }
        return instance;
    }

    private GooglePlacesAPIClient(){
        mRestaurants = new MutableLiveData<>();
        mRestaurantDetails = new MutableLiveData<>();
    }

    public LiveData<List<Restaurant>> getRestaurants(){
        return  mRestaurants;
    }

    public LiveData<Restaurant> getRestaurantDetails(){
        return mRestaurantDetails;
    }
    //setup and run RetrieveNearbyRestaurantsRunnable
    public void nearbySearchRestaurantApi(String location, String radius, String type){
        if (mRetrieveNearbyRestaurantsRunnable != null){
            mRetrieveNearbyRestaurantsRunnable = null;
        }
        mRetrieveNearbyRestaurantsRunnable = new RetrieveNearbyRestaurantsRunnable(location, radius, type);
        final Future handler = AppExecutors.getInstance().getNetworkIO().submit(mRetrieveNearbyRestaurantsRunnable);

        AppExecutors.getInstance().getNetworkIO().schedule(() -> {
            //let the user know it's timed out
            handler.cancel(true);
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void restaurantDetailsApi(String restaurantId, String type){
        if (mRetrieveRestaurantDetailsRunnable != null){
            mRetrieveRestaurantDetailsRunnable = null;
        }
        mRetrieveRestaurantDetailsRunnable = new RetrieveRestaurantDetailsRunnable(restaurantId, type);
        final Future handler = AppExecutors.getInstance().getNetworkIO().submit(mRetrieveRestaurantDetailsRunnable);

        AppExecutors.getInstance().getNetworkIO().schedule(() -> { handler.cancel(true); }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private class RetrieveNearbyRestaurantsRunnable implements Runnable{

        private String location;
        private String radius;
        private String type;
        boolean cancelRequest;

        public RetrieveNearbyRestaurantsRunnable(String location, String radius, String type) {
            this.location = location;
            this.radius = radius;
            this.type = type;
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getRestaurantsFromService(location, radius, type).execute();
                if (cancelRequest){
                    return;
                }
                if (response.code() == 200){
                    List<Restaurant> list = new ArrayList<>(((NearbySearchRestaurantResponse)response.body()).getResult());
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

        private Call<NearbySearchRestaurantResponse> getRestaurantsFromService(String location, String radius, String type){
            return ServiceGenerator.getGooglePlaceApi().nearbySearchRestaurant(API_KEY, location, radius, type);
        }

//        private void cancelRequest(){
//            Log.d(TAG, "cancelRequest: canceling the search request.");
//        }
    }

    private class RetrieveRestaurantDetailsRunnable implements Runnable{

        private String mRestaurantId;
        private String mType;
        boolean cancelRequest;


        public RetrieveRestaurantDetailsRunnable(String restaurantId, String type) {
            this.mRestaurantId = restaurantId;
            this.mType = type;
            this.cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getRestaurantDetailsFromService(mRestaurantId, mType).execute();
                if (cancelRequest){
                    return;
                }
                if (response.code() == 200){
                    Restaurant restaurantWithDetails = ((RestaurantDetailsResponse)response.body()).getResult();
                    mRestaurantDetails.postValue(restaurantWithDetails);
                }else{
                    String error  = response.errorBody().string();
                    Log.e(TAG, "run: "+ error);
                    mRestaurants.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mRestaurants.postValue(null);
            }
        }
    }

    private Call<RestaurantDetailsResponse> getRestaurantDetailsFromService(String restaurantId, String type){
        return ServiceGenerator.getGooglePlaceApi().requestRestaurantDetails(API_KEY, restaurantId, type);
    }
}
