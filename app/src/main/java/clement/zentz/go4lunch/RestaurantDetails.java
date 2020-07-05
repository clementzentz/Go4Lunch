package clement.zentz.go4lunch;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.util.Constants;

public class RestaurantDetails extends AppCompatActivity {

    private static final String TAG = "RestaurantDetails";

    private Restaurant mRestaurant;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);

        getRestaurantFromIncomingIntent();

        fab.setOnClickListener(view -> {
            mRestaurant.setIsReserved(true);
        });
    }

    private void getRestaurantFromIncomingIntent(){
        if (getIntent().hasExtra(Constants.LIST_RESTAURANT_FRAGMENT_TO_RESTAURANT_DETAILS_INTENT)){
            mRestaurant = getIntent().getParcelableExtra(Constants.LIST_RESTAURANT_FRAGMENT_TO_RESTAURANT_DETAILS_INTENT);
        }
    }
}
