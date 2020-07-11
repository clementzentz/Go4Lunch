package clement.zentz.go4lunch.ui.listRestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import clement.zentz.go4lunch.RestaurantDetails;
import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.viewModels.GooglePlacesViewModel;
import clement.zentz.go4lunch.util.Constants;
import clement.zentz.go4lunch.util.ListRestaurantFragmentToListRestaurantAdapter;
import clement.zentz.go4lunch.viewModels.MainActivityViewModel;

public class ListRestaurantFragment extends Fragment implements ListRestaurantFragmentToListRestaurantAdapter {

    private GooglePlacesViewModel mGooglePlacesViewModel;

    private RecyclerView recyclerView;
    private ListRestaurantAdapter adapter;

    private MainActivityViewModel mMainActivityViewModel;
    private Workmate currentUser;

    private static final String TAG = "ListRestaurantFragment";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mGooglePlacesViewModel = ViewModelProviders.of(getActivity()).get(GooglePlacesViewModel.class);

        View root = inflater.inflate(R.layout.fragment_list_restaurant, container, false);

        //recyclerview
        recyclerView = root.findViewById(R.id.list_restaurant_rv);

        subscribeGooglePlaceObserver();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        subscribeCurrentUserObserver();
        setUpRecyclerView();
    }

    private void subscribeGooglePlaceObserver(){
        mGooglePlacesViewModel.getRestaurants().observe(getViewLifecycleOwner(), restaurants -> adapter.setRestaurantList(restaurants));
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
        startActivity(intent);
    }

    private void subscribeCurrentUserObserver(){
        mMainActivityViewModel.getCurrentUser().observe(getViewLifecycleOwner(), workmate -> currentUser = workmate);
    }
}
