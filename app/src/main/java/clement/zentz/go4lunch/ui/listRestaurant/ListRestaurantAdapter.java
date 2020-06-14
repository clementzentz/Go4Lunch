package clement.zentz.go4lunch.ui.listRestaurant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.util.BottomActivityToAdapter;

public class ListRestaurantAdapter extends RecyclerView.Adapter<ListRestaurantAdapter.ListRestaurantViewHolder> {

    public BottomActivityToAdapter mBottomActivityToAdapter;

    public ListRestaurantAdapter(BottomActivityToAdapter bottomActivityToAdapter) {

        mBottomActivityToAdapter = bottomActivityToAdapter;
    }

    @NonNull
    @Override
    public ListRestaurantAdapter.ListRestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant_cardview, parent, false);
        return new ListRestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListRestaurantAdapter.ListRestaurantViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    //ViewHolder
    class ListRestaurantViewHolder extends RecyclerView.ViewHolder{

        public ListRestaurantViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(view -> mBottomActivityToAdapter.launchDetailRestaurantActivity());
        }
    }
}
