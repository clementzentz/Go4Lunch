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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.util.Constants;
import clement.zentz.go4lunch.viewModels.DetailViewModel;

public class AuthActivity extends BaseActivity {

    private static final String TAG = "AuthActivity";
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startAuthUIActivityForResult();

//        List<AuthUI.IdpConfig> providers = Arrays.asList(
//                new AuthUI.IdpConfig.EmailBuilder().build(),
//                new AuthUI.IdpConfig.GoogleBuilder().build(),
//                new AuthUI.IdpConfig.FacebookBuilder().build(),
//                new AuthUI.IdpConfig.TwitterBuilder().build());
//
//        startActivityForResult(
//                AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setAvailableProviders(providers)
//                        .build(),
//                RC_SIGN_IN);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN) {
//            IdpResponse response = IdpResponse.fromResultIntent(data);
//
//            if (resultCode == RESULT_OK) {
//                // Successfully signed in
//                Intent intent = new Intent(this, MainActivity.class);
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//                String userPhotoUrl = null;
//                if (user.getPhotoUrl() != null){
//                    userPhotoUrl = user.getPhotoUrl().toString();
//                }
//                Workmate workmate = new Workmate(
//                        user.getUid(),
//                        user.getDisplayName(),
//                        user.getEmail(),
//                        userPhotoUrl,
//                        "",
//                        Timestamp.now());
//
//                intent.putExtra(Constants.AUTH_ACTIVITY_TO_MAIN_ACTIVITY, workmate);
//                startActivity(intent);
//                // ...
//            }else {
//                // Sign in failed. If response is null the user canceled the
//                // sign-in flow using the back button. Otherwise check
//                // response.getError().getErrorCode() and handle the error.
//                // ...
//            }
//        }
//    }

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
                            String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                            intent.putExtra(Constants.AUTH_ACTIVITY_TO_MAIN_ACTIVITY, currentUserId);
                            startActivity(intent);

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

}
