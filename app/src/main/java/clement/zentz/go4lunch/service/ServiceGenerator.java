package clement.zentz.go4lunch.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class ServiceGenerator {

    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .baseUrl(GooglePlacesApi.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static GooglePlacesApi recipeApi = retrofit.create(GooglePlacesApi.class);

    public static GooglePlacesApi getRecipeApi(){
        return recipeApi;
    }
}
