package clement.zentz.go4lunch.services.googlePlaces;

import clement.zentz.go4lunch.services.googlePlaces.responses.NearbySearchRestaurantResponse;
import clement.zentz.go4lunch.services.googlePlaces.responses.RestaurantDetailsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GooglePlacesApi {

    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/";
    public static final String ENDPOINT_FIND_PLACES = "https://maps.googleapis.com/maps/api/place/findplacefromtext/output?parameters";
    public static final String ENDPOINT_NEARBY_SEARCH = "https://maps.googleapis.com/maps/api/place/nearbysearch/output?parameters";
    public static final String ENDPOINT_PLACE_DETAILS = "https://maps.googleapis.com/maps/api/place/details/output?parameters";
    public static final String ENDPOINT_FIND_PLACES_EXAMPLE = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=Museum%20of%20Contemporary%20Art%20Australia&inputtype=textquery&fields=photos,formatted_address,name,rating,opening_hours,geometry&key=YOUR_API_KEY";


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
}