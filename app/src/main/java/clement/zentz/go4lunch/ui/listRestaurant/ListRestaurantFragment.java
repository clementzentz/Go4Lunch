package clement.zentz.go4lunch.ui.listRestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import clement.zentz.go4lunch.RestaurantDetails;
import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.viewModels.GooglePlacesViewModel;
import clement.zentz.go4lunch.util.Constants;
import clement.zentz.go4lunch.util.ListRestaurantFragmentToListRestaurantAdapter;
import clement.zentz.go4lunch.viewModels.FirebaseViewModel;

public class ListRestaurantFragment extends Fragment implements ListRestaurantFragmentToListRestaurantAdapter {

    private GooglePlacesViewModel mGooglePlacesViewModel;
    private FirebaseViewModel mFirebaseViewModel;

    private RecyclerView recyclerView;
    private ListRestaurantAdapter adapter;

    private Workmate currentUser;
    private List<Workmate> allWorkmates = new ArrayList<>();

    private static final String TAG = "ListRestaurantFragment";

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
        mFirebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);
        mGooglePlacesViewModel = new ViewModelProvider(requireActivity()).get(GooglePlacesViewModel.class);

        subscribeFirebaseObserver();
        subscribeGooglePlaceObserver();
    }

    private void subscribeGooglePlaceObserver(){
        mGooglePlacesViewModel.getRestaurants().observe(getViewLifecycleOwner(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                adapter.setRestaurantList(restaurants);
            }
        });
    }

    private void subscribeFirebaseObserver(){
        mFirebaseViewModel.getCurrentUser().observe(getViewLifecycleOwner(), workmate -> currentUser = workmate);

        mFirebaseViewModel.getWorkmates().observe(getViewLifecycleOwner(), new Observer<List<Workmate>>() {
            @Override
            public void onChanged(List<Workmate> workmates) {
                adapter.setWorkmatesList(workmates);
                allWorkmates.addAll(workmates);
            }
        });
    }

    private void setUpRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new ListRestaurantAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void launchDetailRestaurantActivity(Restaurant currentRestaurant) {
        Intent intent = new Intent(getActivity(), RestaurantDetails.class);
        intent.putExtra(Constants.LIST_RESTAURANT_CURRENT_RESTAURANT_ASK_INTENT, currentRestaurant);
        intent.putExtra(Constants.MAIN_ACTIVITY_CURRENT_USER_ASK_INTENT, currentUser);
        intent.putExtra(Constants.ALL_WORKMATES_INTENT, (Serializable) allWorkmates);
        startActivity(intent);
    }
}
