package clement.zentz.go4lunch.services.googlePlaces;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import clement.zentz.go4lunch.util.appExecutors.AppExecutors;
import clement.zentz.go4lunch.models.placeAutocomplete.Prediction;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.services.googlePlaces.responses.NearbySearchRestaurantResponse;
import clement.zentz.go4lunch.services.googlePlaces.responses.PlaceAutocompleteResponse;
import clement.zentz.go4lunch.services.googlePlaces.responses.RestaurantDetailsResponse;
import retrofit2.Call;
import retrofit2.Response;

import static clement.zentz.go4lunch.util.Constants.API_KEY;
import static clement.zentz.go4lunch.util.Constants.NETWORK_TIMEOUT;

public class GooglePlacesAPIClient {

    private static final String TAG = "GooglePlacesAPIClient";

    private static GooglePlacesAPIClient instance;

    //nearbySearchRequest
    private final MutableLiveData<List<Restaurant>> mRestaurants;
    private final MutableLiveData<String> mPageToken;
    private RetrieveNearbyRestaurantsRunnable mRetrieveNearbyRestaurantsRunnable;

    //placeDetailsRequest
    private final MutableLiveData<Restaurant> mRestaurantDetails;
    private final MutableLiveData<Restaurant> mRestaurantDetails4PlaceAutocomplete;
    private RetrieveRestaurantDetailsRunnable mRetrieveRestaurantDetailsRunnable;

    //placeAutocompleteRequest
    private final MutableLiveData<List<Prediction>> predictionsPlaceAutocomplete;
    private RetrievePlaceAutoCompleteRestaurantRunnable mRetrievePlaceAutoCompleteRestaurantRunnable;

    public static GooglePlacesAPIClient getInstance(){
        if (instance == null){
            instance = new GooglePlacesAPIClient();
        }
        return instance;
    }

    private GooglePlacesAPIClient(){
        mRestaurants = new MutableLiveData<>();
        mPageToken = new MutableLiveData<>();
        mRestaurantDetails = new MutableLiveData<>();
        predictionsPlaceAutocomplete = new MutableLiveData<>();
        mRestaurantDetails4PlaceAutocomplete = new MutableLiveData<>();
    }

    public LiveData<List<Restaurant>> getRestaurants(){
        return  mRestaurants;
    }

    public LiveData<String> getNextPageToken(){
        return mPageToken;
    }

    public LiveData<Restaurant> getRestaurantDetails(){
        return mRestaurantDetails;
    }

    public LiveData<Restaurant> getRestaurantDetails4PlaceAutocomplete(){
        return mRestaurantDetails4PlaceAutocomplete;
    }

    public LiveData<List<Prediction>> getPredictionsPlaceAutocomplete(){
        return predictionsPlaceAutocomplete;
    }

    //setup and run RetrieveNearbyRestaurantsRunnable
    public void searchNearbyRestaurants(String location, String radius, String type, String pageToken){
        if (mRetrieveNearbyRestaurantsRunnable != null){
            mRetrieveNearbyRestaurantsRunnable = null;
        }
        mRetrieveNearbyRestaurantsRunnable = new RetrieveNearbyRestaurantsRunnable(location, radius, type, pageToken);
        final Future handler = AppExecutors.getInstance().getNetworkIO().submit(mRetrieveNearbyRestaurantsRunnable);

        AppExecutors.getInstance().getNetworkIO().schedule(() -> {
            //let the user know it's timed out
            handler.cancel(true);

        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void searchRestaurantDetails(String restaurantId, String type, int code){
        if (mRetrieveRestaurantDetailsRunnable != null){
            mRetrieveRestaurantDetailsRunnable = null;
        }
        mRetrieveRestaurantDetailsRunnable = new RetrieveRestaurantDetailsRunnable(restaurantId, type, code);
        final Future handler = AppExecutors.getInstance().getNetworkIO().submit(mRetrieveRestaurantDetailsRunnable);

        AppExecutors.getInstance().getNetworkIO().schedule(() -> {

            handler.cancel(true);

            }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void searchPlaceAutocompletePredictions(String userInput, String type, String radius, String location){
        if (mRetrievePlaceAutoCompleteRestaurantRunnable != null){
            mRetrievePlaceAutoCompleteRestaurantRunnable = null;
        }
        mRetrievePlaceAutoCompleteRestaurantRunnable = new RetrievePlaceAutoCompleteRestaurantRunnable(userInput, type, radius, location);
        final Future handler = AppExecutors.getInstance().getNetworkIO().submit(mRetrievePlaceAutoCompleteRestaurantRunnable);

        AppExecutors.getInstance().getNetworkIO().schedule(() -> {

            handler.cancel(true);

            }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private class RetrieveNearbyRestaurantsRunnable implements Runnable{

        private final String location;
        private final String radius;
        private final String type;
        private String pageToken;
        boolean cancelRequest;

        public RetrieveNearbyRestaurantsRunnable(String location, String radius, String type, String pageToken) {
            this.location = location;
            this.radius = radius;
            this.type = type;
            this.pageToken = pageToken;
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getRestaurantsFromService(location, radius, type, pageToken).execute();
                if (cancelRequest){
                    return;
                }
                if (response.code() == 200){
                    List<Restaurant> list = new ArrayList<>(((NearbySearchRestaurantResponse)response.body()).getResult());
                    if (pageToken.equals("")){
                        mRestaurants.postValue(list);
                    }else {
                        List<Restaurant> currentRestaurants = mRestaurants.getValue();
                        currentRestaurants.addAll(list);
                        mRestaurants.postValue(currentRestaurants);
                    }
                    pageToken = ((NearbySearchRestaurantResponse) response.body()).getNextPageToken();
                    mPageToken.postValue(pageToken);
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

        private Call<NearbySearchRestaurantResponse> getRestaurantsFromService(String location, String radius, String type, String pageToken){
            return ServiceGenerator.getGooglePlaceApi().nearbySearchRestaurant(API_KEY, location, radius, type, pageToken);
        }

        private void cancelRequest(){
            Log.d(TAG, "cancelRequest: canceling the search request.");
            cancelRequest = true;
        }
    }

    private class RetrieveRestaurantDetailsRunnable implements Runnable{

        private final String mRestaurantId;
        private final String mType;
        private final int code;
        boolean cancelRequest;


        public RetrieveRestaurantDetailsRunnable(String restaurantId, String type, int code) {
            this.mRestaurantId = restaurantId;
            this.mType = type;
            this.code = code;
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
                    if (code == 0){
                        mRestaurantDetails.postValue(restaurantWithDetails);
                    }else if (code == 1){
                        mRestaurantDetails4PlaceAutocomplete.postValue(restaurantWithDetails);
                    }
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

        private Call<RestaurantDetailsResponse> getRestaurantDetailsFromService(String restaurantId, String type){
            return ServiceGenerator.getGooglePlaceApi().requestRestaurantDetails(API_KEY, restaurantId, type);
        }

        private void cancelRequest(){
            Log.d(TAG, "cancelRequest: canceling the search request.");
            cancelRequest = true;
        }
    }

    private class RetrievePlaceAutoCompleteRestaurantRunnable implements Runnable{

        private final String userInput;
        private final String type;
        private final String radius;
        private final String location;
        boolean cancelRequest;

        public RetrievePlaceAutoCompleteRestaurantRunnable(String userInput, String type, String radius, String location) {
            this.userInput = userInput;
            this.type = type;
            this.radius = radius;
            this.location = location;
            this.cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getPlaceAutocompleteFromService(userInput, type, radius, location).execute();
                if (cancelRequest){
                    return;
                }
                if (response.code() == 200){
                    List<Prediction> list = new ArrayList<>(((PlaceAutocompleteResponse)response.body()).getPredictions());
                    //filter avec getType
                    predictionsPlaceAutocomplete.postValue(list);
                }
                else{
                    String error  = response.errorBody().string();
                    Log.e(TAG, "run: "+ error);
                    predictionsPlaceAutocomplete.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                predictionsPlaceAutocomplete.postValue(null);
            }
        }

        private Call<PlaceAutocompleteResponse> getPlaceAutocompleteFromService(String userInput, String type, String radius, String location){
            return ServiceGenerator.getGooglePlaceApi().requestPlaceAutocompleteEstablishment(userInput, type, location, radius, API_KEY);
        }

        private void cancelRequest(){
            Log.d(TAG, "cancelRequest: canceling the search request.");
            cancelRequest = true;
        }
    }

    public void cancelRequest(){
        if (mRetrieveNearbyRestaurantsRunnable != null) {
            mRetrieveNearbyRestaurantsRunnable.cancelRequest();
        }
        if (mRetrieveRestaurantDetailsRunnable != null){
            mRetrieveRestaurantDetailsRunnable.cancelRequest();
        }
        if (mRetrievePlaceAutoCompleteRestaurantRunnable != null){
            mRetrievePlaceAutoCompleteRestaurantRunnable.cancelRequest();
        }
    }
}
