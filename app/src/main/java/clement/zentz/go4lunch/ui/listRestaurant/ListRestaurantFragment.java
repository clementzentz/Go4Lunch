package clement.zentz.go4lunch.ui.listRestaurant;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import clement.zentz.go4lunch.models.rating.GlobalRating;
import clement.zentz.go4lunch.ui.RestaurantDetailsActivity;
import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.models.placeAutocomplete.Prediction;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.util.Constants;
import clement.zentz.go4lunch.viewModels.ListViewModel;
import clement.zentz.go4lunch.viewModels.SharedViewModel;

public class ListRestaurantFragment extends Fragment implements ListRestaurantFragmentToListRestaurantAdapter {

    private SharedViewModel mSharedViewModel;
    private ListViewModel mListViewModel;

    private RecyclerView recyclerView;
    private ListRestaurantAdapter adapter;

    private String currentUserId;
    private Location userLocation;
    private String nextPageToken;

    private static final String TAG = "ListRestaurantFragment";

    MediatorLiveData<Pair<Pair<List<Workmate>, List<GlobalRating>>, List<Restaurant>>> mediatorLiveData = new MediatorLiveData<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_list_restaurant, container, false);

        //recyclerview
        recyclerView = root.findViewById(R.id.list_restaurant_rv);
        setUpRecyclerView();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        mListViewModel = new ViewModelProvider(requireActivity()).get(ListViewModel.class);

        subscribeObservers();
    }

    private void subscribeObservers(){

        mListViewModel.getRestaurantsWithNextPageToken().observe(getViewLifecycleOwner(), new Observer<Pair<List<Restaurant>, String>>() {
            @Override
            public void onChanged(Pair<List<Restaurant>, String> listStringPair) {
                adapter.setAllRestaurants(listStringPair.first);
                nextPageToken = listStringPair.second;
            }
        });

        mSharedViewModel.getCurrentUserId().observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        currentUserId = s;
                    }
                });

        mSharedViewModel.getLocationUser().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                if (location != null) {
                    userLocation = location;
                }
            }
        });

        mListViewModel.getPredictionsPlaceAutocomplete().observe(getViewLifecycleOwner(), new Observer<List<Prediction>>() {
            @Override
            public void onChanged(List<Prediction> predictions) {
                if (predictions != null){
                    List<Restaurant> restaurants = new ArrayList<>();
                    for (Prediction prediction : predictions){
                        if (prediction.getTypes().contains(Constants.PLACES_TYPE)){
                            Restaurant restaurant = new Restaurant();
                            restaurant.setPlaceId(prediction.getPlaceId());
                            restaurant.setName(prediction.getStructuredFormatting().getMainText());
                            restaurant.setVicinity(prediction.getStructuredFormatting().getSecondaryText());
                            restaurants.add(restaurant);
                        }
                    }
                    adapter.setAllRestaurants(restaurants);
                }
            }
        });

    }

    private void setUpRecyclerView(){

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new ListRestaurantAdapter(this);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (recyclerView.canScrollVertically(1)){
                    //search the next page
                    if (userLocation != null && nextPageToken != null){
                        mListViewModel.searchNearbyRestaurants(
                                userLocation.getLatitude()+" , "+userLocation.getLongitude(),
                                String.valueOf(Constants.RADIUS),
                                Constants.PLACES_TYPE,
                                nextPageToken
                        );
                    }
                }
            }
        });
    }

    @Override
    public void launchDetailRestaurantActivity(Restaurant currentRestaurant) {
        Intent intent = new Intent(getActivity(), RestaurantDetailsActivity.class);
        intent.putExtra(Constants.INTENT_CURRENT_RESTAURANT_ID, currentRestaurant.getPlaceId());
        intent.putExtra(Constants.INTENT_CURRENT_USER_ID, currentUserId);
        intent.putExtra(Constants.IS_YOUR_LUNCH, false);
        startActivity(intent);
    }
}
