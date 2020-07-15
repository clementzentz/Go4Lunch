package clement.zentz.go4lunch.ui.workmates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.viewModels.FirestoreViewModel;

public class WorkmatesFragment extends Fragment{

    private static final String TAG = "WorkmatesFragment";

    private RecyclerView recyclerView;
    private WorkmatesAdapter adapter;

    private FirestoreViewModel mFirestoreViewModel;

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
        mFirestoreViewModel = new ViewModelProvider(requireActivity()).get(FirestoreViewModel.class);
        subscribeWorkmatesObserver();
    }

    private void subscribeWorkmatesObserver(){
        mFirestoreViewModel.receiveAllFirestoreWorkmates().observe(getViewLifecycleOwner(), workmates -> adapter.setWorkmateList(workmates));
    }

    private void setupRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new WorkmatesAdapter();
        recyclerView.setAdapter(adapter);
    }
}
