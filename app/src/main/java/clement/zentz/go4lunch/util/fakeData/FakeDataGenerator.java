//package clement.zentz.go4lunch.util.fakeData;
//
//import com.google.firebase.Timestamp;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//
//import clement.zentz.go4lunch.models.rating.GlobalRating;
//import clement.zentz.go4lunch.models.rating.Rating;
//import clement.zentz.go4lunch.models.workmate.Workmate;
//import clement.zentz.go4lunch.util.Constants;
//import clement.zentz.go4lunch.viewModels.DetailViewModel;
//
//public class FakeDataGenerator {
//
//    private static FakeDataGenerator instance;
//
//    public static FakeDataGenerator getInstance(){
//        if(instance == null){
//            instance = new FakeDataGenerator();
//        }
//        return instance;
//    }
//
//    private void getDataFromJsonFile(){
//        try {
//            JSONObject obj1 = new JSONObject(loadJSONFromAsset(1));
//            JSONObject obj2 = new JSONObject(loadJSONFromAsset(2));
//            JSONObject obj3 = new JSONObject(loadJSONFromAsset(3));
//
//            JSONArray m_iArry = obj1.getJSONArray("fakeWorkmates");
//            JSONArray m_jArry = obj2.getJSONArray("fakeRatings");
//            JSONArray m_kArry = obj3.getJSONArray("fakeGlobalRatings");
//
//            for (int i = 0; i < m_iArry.length(); i++) {
//                JSONObject current_jo_inside1 = m_iArry.getJSONObject(i);
//
//                Workmate workmate = new Workmate(
//                        current_jo_inside1.getString(Constants.WORKMATE_ID),
//                        current_jo_inside1.getString(Constants.WORKMATE_NAME),
//                        current_jo_inside1.getString(Constants.WORKMATE_EMAIL),
//                        current_jo_inside1.getString(Constants.WORKMATE_PHOTO_URL),
//                        current_jo_inside1.getString(Constants.RESTAURANT_ID),
//                        Timestamp.now()
//                );
//
//                mFirestoreViewModel.addOrUpdateFirestoreCurrentUser(workmate);
//            }
//
//            for (int j = 0; j < m_jArry.length(); j++){
//
//                JSONObject current_jo_inside2 = m_jArry.getJSONObject(j);
//
//                Rating rating = new Rating(
//                        (double)current_jo_inside2.get(Constants.RATING),
//                        current_jo_inside2.getString(Constants.RESTAURANT_ID),
//                        current_jo_inside2.getString(Constants.WORKMATE_ID)
//                );
//                mFirestoreViewModel.addOrUpdateUserRating(rating);
//            }
//
//            for (int k = 0; k < m_kArry.length(); k++){
//
//                JSONObject current_jo_inside3 = m_kArry.getJSONObject(k);
//
//                GlobalRating globalRating = new GlobalRating(
//                        (double)current_jo_inside3.get(Constants.GLOBAL_RATING),
//                        current_jo_inside3.getString(Constants.RESTAURANT_ID)
//                );
//                mFirestoreViewModel.addOrUpdateGlobalRating(globalRating);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public String loadJSONFromAsset(int num) {
//
//        String json = null;
//        InputStream is;
//        try {
//            if (num == 1){
//                is = this.getAssets().open("fake_workmates.json");
//            }else if (num == 2){
//                is = this.getAssets().open("fake_workmates_ratings.json");
//            }else {
//                is = this.getAssets().open("fake_workmates_global_ratings.json");
//            }
//
//            int size = is.available();
//
//            byte[] buffer = new byte[size];
//
//            is.read(buffer);
//
//            is.close();
//
//            json = new String(buffer, StandardCharsets.UTF_8);
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            return null;
//        }
//        return json;
//    }
//}
