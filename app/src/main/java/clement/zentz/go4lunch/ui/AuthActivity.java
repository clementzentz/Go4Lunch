package clement.zentz.go4lunch.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.models.rating.GlobalRating;
import clement.zentz.go4lunch.models.rating.Rating;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.util.Constants;
import clement.zentz.go4lunch.util.fakeData.DataAssetHelper;
import clement.zentz.go4lunch.viewModels.DetailViewModel;

public class AuthActivity extends BaseActivity {

    private static final String TAG = "AuthActivity";
    private static final int RC_SIGN_IN = 123;

    private DetailViewModel mDetailViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        retrieveDataFromAssets();

        mDetailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);

        startAuthUIActivityForResult();
    }

    private void startAuthUIActivityForResult(){

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build());

        // GetContent creates an ActivityResultLauncher<String> to allow you to pass
        // in the mime type you'd like to allow the user to select
        ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        IdpResponse response = IdpResponse.fromResultIntent(result.getData());
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Handle the Intent
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            Workmate currentUser;
                            String userPhotoUrl = null;
                            if (user != null) {
                                if (user.getPhotoUrl() != null) {
                                    userPhotoUrl = user.getPhotoUrl().toString();
                                }
                                currentUser = new Workmate(
                                        user.getUid(),
                                        user.getDisplayName(),
                                        user.getEmail(),
                                        userPhotoUrl,
                                        "",
                                        Timestamp.now());

                                mDetailViewModel.setCurrentUser(currentUser);
                                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                                intent.putExtra(Constants.AUTH_ACTIVITY_TO_MAIN_ACTIVITY, currentUser.getWorkmateId());
                                startActivity(intent);
                            }

                        }else if (result.getResultCode() == Activity.RESULT_CANCELED){
                            Log.d(TAG, "onActivityResult: user canceled the authentication process.");
                        }else {
                            try {
                                throw Objects.requireNonNull(Objects.requireNonNull(response).getError());
                            } catch (FirebaseUiException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        // Pass in the mime type you'd like to allow the user to select
        // as the input
        mStartForResult.launch(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build());
    }

    private void retrieveDataFromAssets(){

        DataAssetHelper dataAssetHelper = new DataAssetHelper();
        InputStream inputStreamWorkmates = null, inputStreamRatings = null, inputStreamGlobalRatings = null;

        try {
            inputStreamWorkmates = this.getAssets().open("fake_workmates.json");
            inputStreamRatings = this.getAssets().open("fake_workmates_ratings.json");
            inputStreamGlobalRatings = this.getAssets().open("fake_workmates_global_ratings.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Workmate> fakeWorkmateList = (List<Workmate>) dataAssetHelper.getDataFromJsonFile(inputStreamWorkmates, Constants.FAKE_WORKMATES);
        List<Rating> fakeRatingList = (List<Rating>) dataAssetHelper.getDataFromJsonFile(inputStreamRatings, Constants.FAKE_RATINGS);
        List<GlobalRating> fakeGlobalRatingList = (List<GlobalRating>) dataAssetHelper.getDataFromJsonFile(inputStreamGlobalRatings, Constants.FAKE_GLOBAL_RATINGS);

        for (Workmate workmate : fakeWorkmateList){
            mDetailViewModel.setCurrentUser(workmate);
        }
        for (Rating rating : fakeRatingList){
            mDetailViewModel.setUserRating(rating);
        }
        for (GlobalRating globalRating : fakeGlobalRatingList)
            mDetailViewModel.setGlobalRating(globalRating);
    }
}
