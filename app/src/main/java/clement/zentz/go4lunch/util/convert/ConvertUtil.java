package clement.zentz.go4lunch.util.convert;

import com.google.firebase.Timestamp;

import java.util.Map;

import clement.zentz.go4lunch.models.rating.GlobalRating;
import clement.zentz.go4lunch.models.rating.Rating;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.util.Constants;

public class ConvertUtil {

    public Workmate convertMapToWorkmate (Map<String, Object> map){
        return new Workmate(
                (String)map.get(Constants.WORKMATE_ID),
                (String)map.get(Constants.WORKMATE_NAME),
                (String)map.get(Constants.WORKMATE_EMAIL),
                (String)map.get(Constants.WORKMATE_PHOTO_URL),
                (String)map.get(Constants.RESTAURANT_ID),
                (Timestamp)map.get(Constants.TIMESTAMP));
    }

    public Rating convertMapToRating(Map<String, Object> map){
        return new Rating(
                (double) map.get(Constants.RATING),
                (String)map.get(Constants.RESTAURANT_ID),
                (String)map.get(Constants.WORKMATE_ID));
    }

    public GlobalRating convertMapToGlobalRating(Map<String, Object> map){
        return new GlobalRating(
                (double)map.get(Constants.GLOBAL_RATING),
                (String)map.get(Constants.RESTAURANT_ID)
        );
    }
}
