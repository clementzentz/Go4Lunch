package clement.zentz.go4lunch.util.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import clement.zentz.go4lunch.models.placeAutocomplete.Prediction;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.ui.listRestaurant.ListRestaurantAdapter;
import clement.zentz.go4lunch.util.Constants;
import clement.zentz.go4lunch.util.interfaces.SearchViewListDialogToListRestaurantAdapter;
import clement.zentz.go4lunch.viewModels.DetailViewModel;
import clement.zentz.go4lunch.viewModels.ListViewModel;

public class SearchViewListDialogFragment extends DialogFragment implements SearchViewListDialogToListRestaurantAdapter {

    private ListRestaurantAdapter adapter;
    private DetailViewModel mDetailViewModel;
    private ListViewModel mListViewModel;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mDetailViewModel = new ViewModelProvider(requireActivity()).get(DetailViewModel.class);
        mListViewModel = new ViewModelProvider(requireActivity()).get(ListViewModel.class);

        subscribeObserver();

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.custom_recyclerview_dialog, null);
        RecyclerView recyclerView = linearLayout.findViewById(R.id.recyclerview_dialog);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new ListRestaurantAdapter(this);
        recyclerView.setAdapter(adapter);

        builder.setTitle(R.string.pick_a_place)
                .setView(linearLayout)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (getDialog() != null)
                        getDialog().cancel();
                    }
                });

        return builder.create();
    }

    private void subscribeObserver(){
        mListViewModel.getPredictionsPlaceAutocomplete().observe(requireActivity(), new Observer<List<Prediction>>() {
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

    @Override
    public void onRecyclerViewItemClick(Restaurant restaurant) {
        mDetailViewModel.searchRestaurantDetails(restaurant.getPlaceId(), Constants.PLACES_TYPE, 1);
        dismiss();
    }
}
