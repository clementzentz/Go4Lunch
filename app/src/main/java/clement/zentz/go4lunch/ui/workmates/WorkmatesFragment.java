package clement.zentz.go4lunch.ui.workmates;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.ui.DetailActivity;
import clement.zentz.go4lunch.ui.listRestaurant.ListInterfaceToAdapter;
import clement.zentz.go4lunch.util.Constants;
import clement.zentz.go4lunch.viewModels.ListViewModel;
import clement.zentz.go4lunch.viewModels.SharedViewModel;

public class WorkmatesFragment extends Fragment implements ListInterfaceToAdapter {

    private static final String TAG = "WorkmatesFragment";

    private RecyclerView recyclerView;
    private WorkmatesAdapter adapter;

    private ListViewModel mListViewModel;
    private SharedViewModel mSharedViewModel;

    //value
    private String currentUserId;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_workmates, container, false);

        recyclerView = root.findViewById(R.id.workmates_rv);
        setupRecyclerView();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListViewModel = new ViewModelProvider(requireActivity()).get(ListViewModel.class);
        mSharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        subscribeObservers();
    }

    private void subscribeObservers(){

        mListViewModel.getWorkmates().observe(getViewLifecycleOwner(), new Observer<List<Workmate>>() {
            @Override
            public void onChanged(List<Workmate> workmates) {
                adapter.setAllWorkmates(workmates);
            }
        });

        mSharedViewModel.getCurrentUserId().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                currentUserId = s;
            }
        });
    }

    private void setupRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new WorkmatesAdapter(false, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void launchDetailRestaurantActivity(Restaurant currentRestaurant) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(Constants.INTENT_CURRENT_RESTAURANT_ID, currentRestaurant.getPlaceId());
        intent.putExtra(Constants.INTENT_CURRENT_USER_ID, currentUserId);
        intent.putExtra(Constants.IS_YOUR_LUNCH, false);
        startActivity(intent);
    }
}
