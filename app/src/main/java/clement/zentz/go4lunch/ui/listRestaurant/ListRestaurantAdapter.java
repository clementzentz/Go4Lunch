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
import clement.zentz.go4lunch.models.rating.GlobalRating;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.util.Constants;
import clement.zentz.go4lunch.util.interfaces.ListRestaurantFragmentToListRestaurantAdapter;
import clement.zentz.go4lunch.util.interfaces.SearchViewListDialogToListRestaurantAdapter;

public class ListRestaurantAdapter extends RecyclerView.Adapter<ListRestaurantAdapter.ListRestaurantViewHolder> {

    public ListRestaurantFragmentToListRestaurantAdapter mListRestaurantFragmentToListRestaurantAdapter;
    public SearchViewListDialogToListRestaurantAdapter mSearchViewListDialogToListRestaurantAdapter;

    private final List<Restaurant> allRestaurants;
    private final List<Workmate> allWorkmates;
    private final List<Workmate> allWorkmates4ThisRestaurant;
    private final List<GlobalRating> allGlobalRatings;
    private final List<GlobalRating> globalRating4ThisRestaurants;

    public ListRestaurantAdapter(ListRestaurantFragmentToListRestaurantAdapter listRestaurantFragmentToListRestaurantAdapter) {
        mListRestaurantFragmentToListRestaurantAdapter = listRestaurantFragmentToListRestaurantAdapter;
        allWorkmates = new ArrayList<>();
        allWorkmates4ThisRestaurant = new ArrayList<>();
        allRestaurants = new ArrayList<>();
        allGlobalRatings = new ArrayList<>();
        globalRating4ThisRestaurants = new ArrayList<>();
    }

    public ListRestaurantAdapter(SearchViewListDialogToListRestaurantAdapter searchViewListDialogToListRestaurantAdapter){
        mSearchViewListDialogToListRestaurantAdapter = searchViewListDialogToListRestaurantAdapter;
        allWorkmates = new ArrayList<>();
        allWorkmates4ThisRestaurant = new ArrayList<>();
        allRestaurants = new ArrayList<>();
        allGlobalRatings = new ArrayList<>();
        globalRating4ThisRestaurants = new ArrayList<>();
    }

    @NonNull
    @Override
    public ListRestaurantAdapter.ListRestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant_cardview, parent, false);
        return new ListRestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListRestaurantAdapter.ListRestaurantViewHolder holder, int position) {

        holder.restaurantName.setText(allRestaurants.get(position).getName());

        holder.restaurantTypeAddress.setText(allRestaurants.get(position).getVicinity());

        holder.itemView.setOnClickListener(view -> {
            if (allRestaurants.get(position) != null){
                if (mListRestaurantFragmentToListRestaurantAdapter != null){
                    mListRestaurantFragmentToListRestaurantAdapter.launchDetailRestaurantActivity(allRestaurants.get(position));
                }else if (mSearchViewListDialogToListRestaurantAdapter != null){
                    mSearchViewListDialogToListRestaurantAdapter.onRecyclerViewItemClick(allRestaurants.get(position));
                }
            }
        });

        String photoRef = null;
        if (allRestaurants.get(position).getPhotos() != null){
            for (int i = 0; i< allRestaurants.get(position).getPhotos().size() && photoRef == null; i++){
                if (allRestaurants.get(position).getPhotos().get(i) != null){
                    photoRef = allRestaurants.get(position).getPhotos().get(i).getPhotoReference();
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

        for (Workmate workmate : allWorkmates){
            if (allRestaurants.get(position).getPlaceId().equals(workmate.getRestaurantId())){
                allWorkmates4ThisRestaurant.add(workmate);
            }else {
                allWorkmates4ThisRestaurant.remove(workmate);
            }
        }
        holder.workmatesCount.setText(String.valueOf(allWorkmates4ThisRestaurant.size()));

        for (GlobalRating globalRating: allGlobalRatings){
            if (globalRating.getRestaurantId().equals(allRestaurants.get(position).getPlaceId())) {
                globalRating4ThisRestaurants.add(globalRating);
            }else {
                globalRating4ThisRestaurants.remove(globalRating);
            }
        }
        if (!globalRating4ThisRestaurants.isEmpty()){
            holder.ratingBar.setRating((float) globalRating4ThisRestaurants.get(0).getGlobalRating());
        }else{
            holder.ratingBar.setRating(0f);
        }

        if (allRestaurants.get(position).getOpeningHours() == null) {
            holder.restaurantOpenNow.setVisibility(View.GONE);
        }else if (allRestaurants.get(position).getOpeningHours().getOpenNow()){
            holder.restaurantOpenNow.setText("Opened now.");
        }else {
            holder.restaurantOpenNow.setText("Closed now.");
        }
    }

    @Override
    public int getItemCount() {
        return allRestaurants.size();
    }

    public void setAllRestaurants(List<Restaurant> restaurants){
        allRestaurants.clear();
        allRestaurants.addAll(restaurants);
        notifyDataSetChanged();
    }

    public void setAllGlobalRatings(List<GlobalRating> globalRatings){
        allGlobalRatings.clear();
        allGlobalRatings.addAll(globalRatings);
        notifyDataSetChanged();
    }

    public void setAllWorkmates(List<Workmate> workmates){
        allWorkmates.clear();
        allWorkmates.addAll(workmates);
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
