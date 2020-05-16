package clement.zentz.go4lunch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private static final String TAG = "HomeActivity";

    public static final int RC_SIGN_IN = 123;

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.PhoneBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build(),
            new AuthUI.IdpConfig.FacebookBuilder().build(),
            new AuthUI.IdpConfig.TwitterBuilder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_sign_in);


        mAuth = FirebaseAuth.getInstance();

        Button emailBtn = findViewById(R.id.signIn_email);

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: on click ici.");
                try {
                    startSignInActivity();
                }catch (Exception e){
                    Log.d(TAG, "onClick: "+e.toString());
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            updateUI(currentUser);
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        startActivity(new Intent(this, UserLogActivity.class));
    }

    private void startSignInActivity() throws Exception{
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.AppTheme)
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.logo_resto)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                response.getError().getErrorCode();
            }
        }
    }
}

//jsonschema2pojo
//utiliser api googlePlaces
//sdk google map
//ui bottom nav layout
//3 frag
//retrofit
//coordinator layout + scrolling activity pour afficher détail restau
//détail = navigation drawer + new activity pour restaut
