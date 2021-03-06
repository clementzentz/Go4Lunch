package clement.zentz.go4lunch.services.googlePlaces;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static final Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .baseUrl(GooglePlacesApi.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static final Retrofit retrofit = retrofitBuilder.build();

    private static final GooglePlacesApi googlePlaceApi = retrofit.create(GooglePlacesApi.class);

    public static GooglePlacesApi getGooglePlaceApi(){
        return googlePlaceApi;
    }
}
