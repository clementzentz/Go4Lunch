package clement.zentz.go4lunch.ui.listRestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import clement.zentz.go4lunch.MainActivity;
import clement.zentz.go4lunch.RestaurantDetails;
import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.ui.sharedViewModel.GooglePlacesViewModel;
import clement.zentz.go4lunch.util.Constants;
import clement.zentz.go4lunch.util.ListRestaurantFragmentToListRestaurantAdapter;

public class ListRestaurantFragment extends Fragment implements ListRestaurantFragmentToListRestaurantAdapter {

    private GooglePlacesViewModel mGooglePlacesViewModel;

    private RecyclerView recyclerView;
    private ListRestaurantAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mGooglePlacesViewModel = ViewModelProviders.of(getActivity()).get(GooglePlacesViewModel.class);

        View root = inflater.inflate(R.layout.fragment_list_restaurant, container, false);

        //recyclerview
        recyclerView = root.findViewById(R.id.list_restaurant_rv);

        setUpRecyclerView();

        subscribeObservers();

        return root;
    }

    private void subscribeObservers(){
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
        intent.putExtra(Constants.LIST_RESTAURANT_FRAGMENT_TO_RESTAURANT_DETAILS_ASK_INTENT, currentRestaurant);
        getActivity().startActivityForResult(intent, Constants.LIST_RESTAURANT_FRAGMENT_AND_RESTAURANT_DETAILS_REQUEST_CODE);
    }
}
