package clement.zentz.go4lunch.ui.workmates;

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

import clement.zentz.go4lunch.R;

public class WorkmatesFragment extends Fragment {

    private WorkmatesViewModel mWorkmatesViewModel;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        mWorkmatesViewModel =
                ViewModelProviders.of(this).get(WorkmatesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_workmates, container, false);
        /*final TextView textView = root.findViewById(R.id.text_notifications);*/

        recyclerView = root.findViewById(R.id.workmates_rv);
        setupRecyclerView();

        mWorkmatesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                /*textView.setText(s);*/
            }
        });
        return root;
    }

    private void setupRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        WorkmatesAdapter adapter = new WorkmatesAdapter();
        recyclerView.setAdapter(adapter);
    }
}
