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

import clement.zentz.go4lunch.DetailRestaurant;
import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.models.Restaurant;
import clement.zentz.go4lunch.ui.map.MapViewModel;
import clement.zentz.go4lunch.util.BottomActivityToAdapter;

public class ListRestaurantFragment extends Fragment implements BottomActivityToAdapter {

    private ListRestaurantViewModel mListRestaurantViewModel;

    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mListRestaurantViewModel = ViewModelProviders.of(this).get(ListRestaurantViewModel.class);

        View root = inflater.inflate(R.layout.fragment_list_restaurant, container, false);

        recyclerView = root.findViewById(R.id.list_restaurant_rv);
        setUpRecyclerView();

        mListRestaurantViewModel.getText().observe(getViewLifecycleOwner(), s -> {
        });

        return root;
    }

    private void setUpRecyclerView(){
        //fake restaurant test
        Restaurant restaurant = new Restaurant(
                "la pachanga",
                "brazilian", "5 rue des Peupliers",
                "open until 2:00 am",
                "120m",
                null,
                3,
                1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        ListRestaurantAdapter adapter = new ListRestaurantAdapter(restaurant, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void launchDetailRestaurantActivity() {
        startActivity(new Intent(getContext(), DetailRestaurant.class));
    }
}
