package clement.zentz.go4lunch.util;

public abstract class Constants  {

    public static String APP_NAME = "Go4Lunch";

    //api
    public static final int NETWORK_TIMEOUT = 10000;
    public static final String API_KEY = "AIzaSyB-Enm6PIJRhyaQuTuMswPetf12jOI-_Iw";
//    public static final String build_time = ;

    //nearbySearch
    public static final int RADIUS = 1000;
    public static final String PLACES_TYPE = "restaurant";

    //photo
    public static final String BASE_URL_PHOTO_PLACE = "https://maps.googleapis.com/maps/api/place/photo?";
    public static final int MAX_WIDTH_PHOTO = 100;
    public static final int MAX_HEIGHT_PHOTO = 100;

    //Intent
    public static final String RESTAURANT_DETAILS_CURRENT_RESTAURANT_ID = "currentRestaurant";
    public static final String RESTAURANT_DETAILS_CURRENT_USER_ID = "currentUser";
    public static final String AUTH_ACTIVITY_TO_MAIN_ACTIVITY = "user";
    public static final String IS_YOUR_LUNCH = "IS_YOUR_LUNCH";

    //request_code
    public static final int CALL_PERMISSION_REQUEST_CODE = 2;

    //Firestore map key
    public static final String WORKMATES_COLLECTION = "Workmates";
    public static final String RATINGS_COLLECTION = "Ratings";
    public static final String WORKMATE_ID = "workmate_id";
    public static final String WORKMATE_NAME = "workmate_name";
    public static final String WORKMATE_EMAIL = "workmate_email";
    public static final String WORKMATE_PHOTO_URL = "workmate_photo_url";
    public static final String RESTAURANT_ID = "restaurant_id";
    public static final String RESTAURANT_NAME = "restaurant_name";
    public static final String RESTAURANT_ADDRESS = "restaurant_address";
    public static final String RESTAURANT_RATING = "restaurant_rating";
    public static final String TIMESTAMP = "timestamp";

    //notifications
    public static final String CHANNEL_ID = "CHANNEL_ID";
    public static final int notificationId = 123;
}
