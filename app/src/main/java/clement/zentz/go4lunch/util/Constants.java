package clement.zentz.go4lunch.util;

import clement.zentz.go4lunch.R;

public class Constants {
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
    public static final String LIST_RESTAURANT_FRAGMENT_TO_RESTAURANT_DETAILS_INTENT = "restaurant1";
    public static final String AUTH_ACTIVITY_TO_MAIN_ACTIVITY = "user";

    //RequestCode
    public static final int LIST_RESTAURANT_FRAGMENT_AND_RESTAURANT_DETAILS_REQUEST_CODE = 2;
}
