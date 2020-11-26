package clement.zentz.go4lunch.ui.listRestaurant;

import android.content.Intent;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clement.zentz.go4lunch.models.rating.GlobalRating;
import clement.zentz.go4lunch.ui.RestaurantDetailsActivity;
import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.models.placeAutocomplete.Prediction;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.viewModels.FirestoreViewModel;
import clement.zentz.go4lunch.viewModels.GooglePlacesViewModel;
import clement.zentz.go4lunch.util.Constants;
import clement.zentz.go4lunch.util.interfaces.ListRestaurantFragmentToListRestaurantAdapter;
import clement.zentz.go4lunch.viewModels.SharedViewModel;

public class ListRestaurantFragment extends Fragment implements ListRestaurantFragmentToListRestaurantAdapter {

    private GooglePlacesViewModel mGooglePlacesViewModel;
    private SharedViewModel mSharedViewModel;
    private FirestoreViewModel firestoreViewModel;

    private RecyclerView recyclerView;
    private ListRestaurantAdapter adapter;

    private Workmate currentUser;

    private static final String TAG = "ListRestaurantFragment";

    private boolean hasWorkmates=false, hasRestaurants=false, hasGlobalRatings=false;
    private List<Workmate> allWorkmates;
    private List<Restaurant> allRestaurants;
    private List<GlobalRating> allGlobalRatings;

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
        mGooglePlacesViewModel = new ViewModelProvider(requireActivity()).get(GooglePlacesViewModel.class);
        firestoreViewModel = new ViewModelProvider(requireActivity()).get(FirestoreViewModel.class);

        subscribeObservers();
    }

    private void subscribeObservers(){

        mediatorLiveData.addSource(firestoreViewModel.receiveAllFirestoreWorkmates(), new Observer<List<Workmate>>() {
            @Override
            public void onChanged(List<Workmate> workmates) {
                if (workmates != null){
                    hasWorkmates = true;
                    allWorkmates = workmates;
                    updateIfAll();
                }
            }
        });

        mediatorLiveData.addSource(firestoreViewModel.receiveAllGlobalRatings(), new Observer<List<GlobalRating>>() {
            @Override
            public void onChanged(List<GlobalRating> globalRatings) {
                if (globalRatings != null){
                    hasGlobalRatings = true;
                    allGlobalRatings = globalRatings;
                    updateIfAll();
                }
            }
        });

        mediatorLiveData.addSource(mGooglePlacesViewModel.getRestaurants(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurantList) {
                if (restaurantList != null){
                    hasRestaurants = true;
                    allRestaurants = restaurantList;
                    updateIfAll();
                }
            }
        });

        mediatorLiveData.observe(getViewLifecycleOwner(), new Observer<Pair<Pair<List<Workmate>, List<GlobalRating>>, List<Restaurant>>>() {
                    @Override
                    public void onChanged(Pair<Pair<List<Workmate>, List<GlobalRating>>, List<Restaurant>> pairListPair) {
                        adapter.setAllRestaurants(pairListPair.second);
                        adapter.setAllWorkmates(pairListPair.first.first);
                        adapter.setAllGlobalRatings(pairListPair.first.second);
                    }
        });

        mSharedViewModel.getCurrentUser().observe(getViewLifecycleOwner(), workmate -> currentUser = workmate);

        mGooglePlacesViewModel.getPredictionsPlaceAutocomplete().observe(getViewLifecycleOwner(), new Observer<List<Prediction>>() {
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

    private void updateIfAll(){
        if (hasWorkmates && hasGlobalRatings && hasRestaurants){
            mediatorLiveData.postValue(Pair.create(Pair.create(allWorkmates, allGlobalRatings), allRestaurants));
        }
    }

    private void setUpRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new ListRestaurantAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void launchDetailRestaurantActivity(Restaurant currentRestaurant) {
        Intent intent = new Intent(getActivity(), RestaurantDetailsActivity.class);
        intent.putExtra(Constants.RESTAURANT_DETAILS_CURRENT_RESTAURANT_ID, currentRestaurant.getPlaceId());
        intent.putExtra(Constants.RESTAURANT_DETAILS_CURRENT_USER_ID, currentUser.getWorkmateId());
        intent.putExtra(Constants.IS_YOUR_LUNCH, false);
        startActivity(intent);
    }
}
