package clement.zentz.go4lunch.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.RestaurantDetails;
import clement.zentz.go4lunch.models.placeAutocomplete.Prediction;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.ui.listRestaurant.ListRestaurantAdapter;
import clement.zentz.go4lunch.viewModels.GooglePlacesViewModel;

public class SearchViewListDialogFragment extends DialogFragment implements ListRestaurantFragmentToListRestaurantAdapter {

    private ListRestaurantAdapter adapter;
    private GooglePlacesViewModel mGooglePlacesViewModel;

    public SearchViewListDialogFragment(){

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mGooglePlacesViewModel = new ViewModelProvider(requireActivity()).get(GooglePlacesViewModel.class);

        subscribeObserver();

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.custom_recyclerview_dialog, null);
        RecyclerView recyclerView = linearLayout.findViewById(R.id.recyclerview_dialog);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new ListRestaurantAdapter(this);
        recyclerView.setAdapter(adapter);

        builder.setTitle(R.string.pick_a_place)
                .setView(linearLayout);

        return builder.create();
    }

    private void subscribeObserver(){
        mGooglePlacesViewModel.getPredictionsPlaceAutocomplete().observe(requireActivity(), new Observer<List<Prediction>>() {
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
                    adapter.setRestaurantList(restaurants);
                }
            }
        });
    }

    @Override
    public void launchDetailRestaurantActivity(Restaurant restaurant) {
        //request restaurant details to add marker on map
        mGooglePlacesViewModel.restaurantDetails(restaurant.getPlaceId(), Constants.PLACES_TYPE, 2);
    }
}
