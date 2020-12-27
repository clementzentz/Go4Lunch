package clement.zentz.go4lunch.util.fakeData;

import com.google.firebase.Timestamp;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import clement.zentz.go4lunch.models.rating.GlobalRating;
import clement.zentz.go4lunch.models.rating.Rating;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.util.Constants;

public class DataAssetHelper {

    private static DataAssetHelper instance;

    public static DataAssetHelper getInstance(){
        if(instance == null){
            instance = new DataAssetHelper();
        }
        return instance;
    }

    public Object getDataFromJsonFile(InputStream inputStream, String jsonFileName){

        Object object  = null;

        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset(inputStream));

            JSONArray jsonArray = jsonObject.getJSONArray(Constants.FAKE_RATINGS);

            if (jsonFileName.equals(Constants.FAKE_WORKMATES)){
                List<Workmate> workmates = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject current_jo_inside1 = jsonArray.getJSONObject(i);

                    workmates.add(new Workmate(
                            current_jo_inside1.getString(Constants.WORKMATE_ID),
                            current_jo_inside1.getString(Constants.WORKMATE_NAME),
                            current_jo_inside1.getString(Constants.WORKMATE_EMAIL),
                            current_jo_inside1.getString(Constants.WORKMATE_PHOTO_URL),
                            current_jo_inside1.getString(Constants.RESTAURANT_ID),
                            Timestamp.now()
                    ));
                }
                object = workmates;

            }else if (jsonFileName.equals(Constants.FAKE_RATINGS)){
                List<Rating> ratings = new ArrayList<>();
                for (int j = 0; j < jsonArray.length(); j++){

                    JSONObject current_jo_inside2 = jsonArray.getJSONObject(j);

                    ratings.add(new Rating(
                            (double)current_jo_inside2.get(Constants.RATING),
                            current_jo_inside2.getString(Constants.RESTAURANT_ID),
                            current_jo_inside2.getString(Constants.WORKMATE_ID)
                    ));
                }
                object = ratings;

            }else {
                List<GlobalRating> globalRatings = new ArrayList<>();
                for (int k = 0; k < jsonArray.length(); k++){

                    JSONObject current_jo_inside3 = jsonArray.getJSONObject(k);

                    globalRatings.add(new GlobalRating(
                            (double)current_jo_inside3.get(Constants.GLOBAL_RATING),
                            current_jo_inside3.getString(Constants.RESTAURANT_ID)
                    ));
                }
                object = globalRatings;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public String loadJSONFromAsset(InputStream is) {

        String json = null;

        try {
            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, StandardCharsets.UTF_8);

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
