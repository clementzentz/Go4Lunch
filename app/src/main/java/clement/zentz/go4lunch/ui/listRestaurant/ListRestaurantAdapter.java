package clement.zentz.go4lunch.ui.listRestaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.util.BottomActivityToAdapter;

public class ListRestaurantAdapter extends RecyclerView.Adapter<ListRestaurantAdapter.ListRestaurantViewHolder> {

    public BottomActivityToAdapter mBottomActivityToAdapter;
    private List<Restaurant> mRestaurantList;

    private Context mContext;

    public ListRestaurantAdapter(Context context,BottomActivityToAdapter bottomActivityToAdapter, List<Restaurant> restaurants) {

        mBottomActivityToAdapter = bottomActivityToAdapter;
        mRestaurantList = restaurants;
    }

    @NonNull
    @Override
    public ListRestaurantAdapter.ListRestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant_cardview, parent, false);
        return new ListRestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListRestaurantAdapter.ListRestaurantViewHolder holder, int position) {
        holder.restaurantName.setText(mRestaurantList.get(position).getName());

        holder.restaurantTypeAddress.setText(mRestaurantList.get(position).getVicinity());

        Glide.with(mContext)
                .asBitmap()
                .load(mRestaurantList.get(position).getPhotos().get(1))
                .into(holder.restaurantPhoto);
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }

    //ViewHolder
    class ListRestaurantViewHolder extends RecyclerView.ViewHolder{

        TextView restaurantName;
        TextView restaurantTypeAddress;
        ImageView restaurantPhoto;

        public ListRestaurantViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(view -> mBottomActivityToAdapter.launchDetailRestaurantActivity());

            restaurantName = itemView.findViewById(R.id.restaurant_name_txt);
            restaurantTypeAddress = itemView.findViewById(R.id.restaurant_type_address_txt);
            restaurantPhoto = itemView.findViewById(R.id.restaurant_img);
        }
    }
}
