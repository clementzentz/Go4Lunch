package clement.zentz.go4lunch.service;

import android.location.Location;

import com.twitter.sdk.android.core.services.params.Geocode;

import java.util.List;

import clement.zentz.go4lunch.models.Restaurant;
import clement.zentz.go4lunch.models.Workmate;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface GooglePlacesApi {

    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/place";
    public static final String ENDPOINT_FIND_PLACES = "https://maps.googleapis.com/maps/api/place/findplacefromtext/output?parameters";
    public static final String ENDPOINT_NEARBY_SEARCH = "https://maps.googleapis.com/maps/api/place/nearbysearch/output?parameters";
    public static final String ENDPOINT_FIND_PLACES_EXAMPLE = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=Museum%20of%20Contemporary%20Art%20Australia&inputtype=textquery&fields=photos,formatted_address,name,rating,opening_hours,geometry&key=YOUR_API_KEY";


    @GET("/findplacefromtext/json")
    List<Restaurant> nearbySearchRestaurant(
            @Query("key") String apiKey,
            @Query("location") Location location,
            @Query("radius") Geocode.Distance distance //rankBy
    );

    @GET("/users/{user}/repos")
    List<Workmate> getWorkmates(@Path("user") String user);
}