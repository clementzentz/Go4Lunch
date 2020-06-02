package clement.zentz.go4lunch.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.models.Restaurant;

public class ListRestaurantAdapter extends RecyclerView.Adapter<ListRestaurantAdapter.ListRestaurantViewHolder> {

    private Restaurant[] mRestaurants;

    public ListRestaurantAdapter(Restaurant restaurant) {
        mRestaurants = new  Restaurant[1];
        mRestaurants[0] = restaurant;
    }

    @NonNull
    @Override
    public ListRestaurantAdapter.ListRestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_restaurant_rv, parent, false);
        return new ListRestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListRestaurantAdapter.ListRestaurantViewHolder holder, int position) {
        holder.restaurantName.setText(mRestaurants[0].getRestaurantName());
        holder.restaurantAddress.setText(mRestaurants[0].getRestaurantAddress());
        holder.restaurantDistance.setText(mRestaurants[0].getRestaurantDistance());
        holder.restaurantType.setText(mRestaurants[0].getRestaurantType());
        holder.restaurantClock.setText(mRestaurants[0].getRestaurantClock());
    }

    @Override
    public int getItemCount() {
        return mRestaurants.length;
    }

    static class ListRestaurantViewHolder extends RecyclerView.ViewHolder{

        private TextView restaurantName, restaurantAddress, restaurantDistance, restaurantType, restaurantClock;

        private ImageView restaurantImg;

        public ListRestaurantViewHolder(@NonNull View itemView) {
            super(itemView);

            restaurantName = itemView.findViewById(R.id.restaurant_name_txt);
            restaurantAddress = itemView.findViewById(R.id.restaurant_address_txt);
            restaurantDistance = itemView.findViewById(R.id.restaurant_distance_txt);
            restaurantImg = itemView.findViewById(R.id.restaurant_img);
            restaurantType = itemView.findViewById(R.id.restaurant_type_txt);
            restaurantClock = itemView.findViewById(R.id.restaurant_clock);
            restaurantImg.setImageResource(R.drawable.ic_launcher_foreground);

        }
    }
}
