package clement.zentz.go4lunch.util;

import android.content.Context;

import clement.zentz.go4lunch.R;

public abstract class Constants  {
    //api
    public static final int NETWORK_TIMEOUT = 5000;
    public static final String API_KEY = "AIzaSyB-Enm6PIJRhyaQuTuMswPetf12jOI-_Iw";

    //nearbySearch
    public static final int RADIUS = 1000;
    public static final String PLACES_TYPE = "restaurant";

    //photo
    public static final String BASE_URL_PHOTO_PLACE = "https://maps.googleapis.com/maps/api/place/photo?";
    public static final int MAX_WIDTH_PHOTO = 100;
    public static final int MAX_HEIGHT_PHOTO = 100;

    //Intent
    public static final String LIST_RESTAURANT_CURRENT_RESTAURANT_ASK_INTENT = "currentRestaurant";
    public static final String MAIN_ACTIVITY_CURRENT_USER_ASK_INTENT = "currentUser";
    public static final String ALL_WORKMATES_INTENT = "allWorkmates";
    public static final String AUTH_ACTIVITY_TO_MAIN_ACTIVITY = "user";
}
