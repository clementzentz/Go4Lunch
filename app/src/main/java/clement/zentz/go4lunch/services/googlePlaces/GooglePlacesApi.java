package clement.zentz.go4lunch.services.googlePlaces;

import clement.zentz.go4lunch.services.googlePlaces.responses.NearbySearchRestaurantResponse;
import clement.zentz.go4lunch.services.googlePlaces.responses.PlaceAutocompleteResponse;
import clement.zentz.go4lunch.services.googlePlaces.responses.RestaurantDetailsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GooglePlacesApi {

    String BASE_URL = "https://maps.googleapis.com/maps/api/place/";

    @GET("nearbysearch/json")
    Call<NearbySearchRestaurantResponse> nearbySearchRestaurant(
            //required parameters
            @Query("key") String key, //api key
            @Query(value = "location", encoded = true) String location, //location of the current user
            @Query("radius") String radius, //radius of the restaurant to be search
            //optional parameters
            @Query("type") String type //type of searched elements
    );

    @GET("details/json")
    Call<RestaurantDetailsResponse> requestRestaurantDetails(
            @Query("key") String key,
            @Query("place_id") String placeId,
            @Query("type") String type
    );

    @GET("autocomplete/json")
    Call<PlaceAutocompleteResponse> requestPlaceAutocompleteEstablishment(
            @Query("input") String input,
            @Query("type") String type,
            @Query(value = "location", encoded = true) String location,
            @Query("radius") String radius,
            @Query("key") String key
    );
}