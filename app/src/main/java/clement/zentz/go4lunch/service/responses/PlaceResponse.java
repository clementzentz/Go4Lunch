package clement.zentz.go4lunch.service.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import clement.zentz.go4lunch.models.Restaurant;

class PlaceResponse {

    @SerializedName("restaurant")
    @Expose()
    private Restaurant mRestaurant;

    public Restaurant getRestaurant(){
        return mRestaurant;
    }
}
