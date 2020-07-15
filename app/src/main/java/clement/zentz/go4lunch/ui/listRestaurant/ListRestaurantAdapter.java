package clement.zentz.go4lunch.ui.listRestaurant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.util.Constants;
import clement.zentz.go4lunch.util.ListRestaurantFragmentToListRestaurantAdapter;

public class ListRestaurantAdapter extends RecyclerView.Adapter<ListRestaurantAdapter.ListRestaurantViewHolder> {

    public ListRestaurantFragmentToListRestaurantAdapter mListRestaurantFragmentToListRestaurantAdapter;
    private List<Restaurant> mRestaurantList;
    private List<Workmate> mWorkmateList;

    public ListRestaurantAdapter(ListRestaurantFragmentToListRestaurantAdapter listRestaurantFragmentToListRestaurantAdapter) {
        mListRestaurantFragmentToListRestaurantAdapter = listRestaurantFragmentToListRestaurantAdapter;
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

        holder.itemView.setOnClickListener(view -> {
            if (mRestaurantList.get(position) != null){
                mListRestaurantFragmentToListRestaurantAdapter.launchDetailRestaurantActivity(mRestaurantList.get(position));
            }
        });

        Picasso.get().load(Constants.BASE_URL_PHOTO_PLACE
                + "key=" + Constants.API_KEY
                + "&maxwidth="+Constants.MAX_WIDTH_PHOTO
                + "&maxheight="+Constants.MAX_HEIGHT_PHOTO
                + "&photoreference=" + (mRestaurantList.get(position).getPhotos().get(0).getPhotoReference()))
                .into(holder.restaurantPhoto);

        int count = 0;
        for (Workmate workmate : mWorkmateList){
            if (mRestaurantList.get(position).getPlaceId().equals(workmate.getRestaurantId())){
                holder.workmatesCount.setText("("+ (count += 1) +")");
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mRestaurantList != null){
            return mRestaurantList.size();
        }else {
            return 0;
        }
    }

    public void setRestaurantList(List<Restaurant> restaurants){
        mRestaurantList = restaurants;
        notifyDataSetChanged();
    }

    public void setWorkmatesList(List<Workmate> workmates){
        mWorkmateList = workmates;
        notifyDataSetChanged();
    }

    //ViewHolder
    static class ListRestaurantViewHolder extends RecyclerView.ViewHolder{

        TextView restaurantName;
        TextView restaurantTypeAddress;
        ImageView restaurantPhoto;
        TextView workmatesCount;

        public ListRestaurantViewHolder(@NonNull View itemView) {
            super(itemView);

            restaurantName = itemView.findViewById(R.id.restaurant_name_txt);
            restaurantTypeAddress = itemView.findViewById(R.id.restaurant_type_address_txt);
            restaurantPhoto = itemView.findViewById(R.id.restaurant_photo);
            workmatesCount = itemView.findViewById(R.id.workmates_count_txt);
        }
    }
}
