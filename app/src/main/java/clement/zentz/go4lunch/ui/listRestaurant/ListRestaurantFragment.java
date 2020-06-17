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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import clement.zentz.go4lunch.DetailRestaurant;
import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.ui.map.MapViewModel;
import clement.zentz.go4lunch.util.BottomActivityToAdapter;

public class ListRestaurantFragment extends Fragment implements BottomActivityToAdapter {

    private ListRestaurantViewModel mListRestaurantViewModel;
    private MapViewModel mMapViewModel;
    private List<Restaurant> mRestaurantList;

    private RecyclerView recyclerView;
    private ListRestaurantAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mListRestaurantViewModel = ViewModelProviders.of(this).get(ListRestaurantViewModel.class);
        mMapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);

        View root = inflater.inflate(R.layout.fragment_list_restaurant, container, false);

        //recyclerview
        recyclerView = root.findViewById(R.id.list_restaurant_rv);
        setUpRecyclerView();

        mMapViewModel.getListMutableLiveData().observe(getViewLifecycleOwner(), restaurants -> {
            mRestaurantList = restaurants;
            adapter.notifyDataSetChanged();
        });

        return root;
    }

    private void setUpRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new ListRestaurantAdapter(getContext(),this, mRestaurantList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void launchDetailRestaurantActivity() {
        startActivity(new Intent(getContext(), DetailRestaurant.class));
    }
}
