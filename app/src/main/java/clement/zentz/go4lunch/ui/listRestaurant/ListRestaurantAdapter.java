package clement.zentz.go4lunch.ui.listRestaurant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.util.Constants;
import clement.zentz.go4lunch.util.interfaces.ListRestaurantFragmentToListRestaurantAdapter;
import clement.zentz.go4lunch.util.interfaces.SearchViewListDialogToListRestaurantAdapter;

public class ListRestaurantAdapter extends RecyclerView.Adapter<ListRestaurantAdapter.ListRestaurantViewHolder> {

    public ListRestaurantFragmentToListRestaurantAdapter mListRestaurantFragmentToListRestaurantAdapter;
    public SearchViewListDialogToListRestaurantAdapter mSearchViewListDialogToListRestaurantAdapter;
    private List<Restaurant> mRestaurantList;
    private List<Workmate> mWorkmateList;

    public ListRestaurantAdapter(ListRestaurantFragmentToListRestaurantAdapter listRestaurantFragmentToListRestaurantAdapter) {
        mListRestaurantFragmentToListRestaurantAdapter = listRestaurantFragmentToListRestaurantAdapter;
        mWorkmateList = new ArrayList<>();
        mRestaurantList = new ArrayList<>();
    }

    public ListRestaurantAdapter(SearchViewListDialogToListRestaurantAdapter searchViewListDialogToListRestaurantAdapter){
        mSearchViewListDialogToListRestaurantAdapter = searchViewListDialogToListRestaurantAdapter;
        mWorkmateList = new ArrayList<>();
        mRestaurantList = new ArrayList<>();
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
                if (mListRestaurantFragmentToListRestaurantAdapter != null){
                    mListRestaurantFragmentToListRestaurantAdapter.launchDetailRestaurantActivity(mRestaurantList.get(position));
                }else if (mSearchViewListDialogToListRestaurantAdapter != null){
                    mSearchViewListDialogToListRestaurantAdapter.onRecyclerViewItemClick(mRestaurantList.get(position));
                }
            }
        });


        String photoRef = null;
        if (mRestaurantList.get(position).getPhotos() != null){
            for (int i=0; i<mRestaurantList.get(position).getPhotos().size() && photoRef == null; i++){
                if (mRestaurantList.get(position).getPhotos().get(i) != null){
                    photoRef = mRestaurantList.get(position).getPhotos().get(i).getPhotoReference();
                }
            }
            if (photoRef != null){
                Picasso.get().load(Constants.BASE_URL_PHOTO_PLACE
                        + "key=" + Constants.API_KEY
                        + "&maxwidth="+Constants.MAX_WIDTH_PHOTO
                        + "&maxheight="+Constants.MAX_HEIGHT_PHOTO
                        + "&photoreference=" + (photoRef))
                        .into(holder.restaurantPhoto);
            }else {
                holder.restaurantPhoto.setVisibility(View.GONE);
            }
        }

        int count = 0;
        for (Workmate workmate : mWorkmateList){
            if (mRestaurantList.get(position).getPlaceId().equals(workmate.getRestaurantId())){
                holder.workmatesCount.setText("("+ (count += 1) +")");
            }
        }if (count == 0){
            holder.workmatesCount.setVisibility(View.GONE);
            holder.workmatesBtn.setVisibility(View.GONE);
        }

        if (mRestaurantList.get(position).getRating() != null){
            float rating = (float)((mRestaurantList.get(position).getRating().floatValue())*(3.0/5.0));
            holder.ratingBar.setRating(rating);
        }else {
            holder.ratingBar.setVisibility(View.GONE);
        }

        if (mRestaurantList.get(position).getOpeningHours() == null) {
            holder.restaurantOpenNow.setVisibility(View.GONE);
        }else if (mRestaurantList.get(position).getOpeningHours().getOpenNow()){
            holder.restaurantOpenNow.setText("Opened now.");
        }else {
            holder.restaurantOpenNow.setText("Closed now.");
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
        mRestaurantList.clear();
        mRestaurantList.addAll(restaurants);
        notifyDataSetChanged();
    }

    public void setWorkmatesList(List<Workmate> workmates){
        mWorkmateList.clear();
        mWorkmateList.addAll(workmates);
        notifyDataSetChanged();
    }

    //ViewHolder
    static class ListRestaurantViewHolder extends RecyclerView.ViewHolder{

        TextView restaurantName;
        TextView restaurantTypeAddress;
        TextView restaurantOpenNow;
        ImageView restaurantPhoto;
        ImageButton workmatesBtn;
        TextView workmatesCount;
        RatingBar ratingBar;

        public ListRestaurantViewHolder(@NonNull View itemView) {
            super(itemView);

            restaurantName = itemView.findViewById(R.id.restaurant_name_txt);
            restaurantTypeAddress = itemView.findViewById(R.id.restaurant_type_address_txt);
            restaurantOpenNow = itemView.findViewById(R.id.restaurant_clock);
            restaurantPhoto = itemView.findViewById(R.id.restaurant_photo);
            workmatesBtn = itemView.findViewById(R.id.workmates_btn);
            workmatesCount = itemView.findViewById(R.id.workmates_count_txt);
            ratingBar = itemView.findViewById(R.id.rating_bar_indicator_item);
        }
    }
}
