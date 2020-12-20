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
import clement.zentz.go4lunch.util.interfaces.SearchViewListDialogToListRestaurantAdapter;

import static clement.zentz.go4lunch.util.Constants.EXHAUSTED_TYPE;
import static clement.zentz.go4lunch.util.Constants.LOADING_TYPE;
import static clement.zentz.go4lunch.util.Constants.RESTAURANT_TYPE;

public class ListRestaurantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public ListRestaurantFragmentToListRestaurantAdapter mListRestaurantFragmentToListRestaurantAdapter;
    public SearchViewListDialogToListRestaurantAdapter mSearchViewListDialogToListRestaurantAdapter;

    private List<Restaurant> allRestaurants;

    public ListRestaurantAdapter(ListRestaurantFragmentToListRestaurantAdapter listRestaurantFragmentToListRestaurantAdapter) {
        mListRestaurantFragmentToListRestaurantAdapter = listRestaurantFragmentToListRestaurantAdapter;
        allRestaurants = new ArrayList<>();
    }

    public ListRestaurantAdapter(SearchViewListDialogToListRestaurantAdapter searchViewListDialogToListRestaurantAdapter){
        mSearchViewListDialogToListRestaurantAdapter = searchViewListDialogToListRestaurantAdapter;
        allRestaurants = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        switch (viewType){

            case RESTAURANT_TYPE:{
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant_cardview, parent, false);
                return new ListRestaurantViewHolder(view);
            }

            case EXHAUSTED_TYPE:{
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_exhausted, parent, false);
                return new SearchExhaustedViewHolder(view);
            }

            default:{
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_list_item, parent, false);
                return new LoadingViewHolder(view);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((ListRestaurantViewHolder)holder).restaurantName.setText(allRestaurants.get(position).getName());

        ((ListRestaurantViewHolder)holder).restaurantTypeAddress.setText(allRestaurants.get(position).getVicinity());

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
                        .into(((ListRestaurantViewHolder)holder).restaurantPhoto);
            }else {
                ((ListRestaurantViewHolder)holder).restaurantPhoto.setVisibility(View.GONE);
            }
        }

        ((ListRestaurantViewHolder)holder).workmatesCount.setText(String.valueOf(allRestaurants.get(position).getWorkmatesJoining().size()));

        ((ListRestaurantViewHolder)holder).ratingBar.setRating((float) allRestaurants.get(position).getGlobalRating());

        if (allRestaurants.get(position).getOpeningHours() == null) {
            ((ListRestaurantViewHolder)holder).restaurantOpenNow.setVisibility(View.GONE);
        }else if (allRestaurants.get(position).getOpeningHours().getOpenNow()){
            ((ListRestaurantViewHolder)holder).restaurantOpenNow.setText("Opened now.");
        }else {
            ((ListRestaurantViewHolder)holder).restaurantOpenNow.setText("Closed now.");
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (allRestaurants.get(position).getName().equals("LOADING...")){
            return LOADING_TYPE;
        }
        else if (allRestaurants.get(position).getName().equals("EXHAUSTED...")){
            return EXHAUSTED_TYPE;
        }
        else if (position == allRestaurants.size() - 1
                && position != 0
                && !allRestaurants.get(position).getName().equals("EXHAUSTED...")){
            return LOADING_TYPE;
        }
        else {
            return RESTAURANT_TYPE;
        }
    }

    public void setQueryExhausted(){
        hideLoading();
        Restaurant exhaustedRestaurant = new Restaurant();
        exhaustedRestaurant.setName("EXHAUSTED...");
        allRestaurants.add(exhaustedRestaurant);
        notifyDataSetChanged();
    }

    private void hideLoading(){
        if (isLoading()){
            for (Restaurant restaurant : allRestaurants){
                if (restaurant.getName().equals("LOADING...")){
                    allRestaurants.remove(restaurant);
                }
            }
            notifyDataSetChanged();
        }
    }

    public void displayLoading(){
        if (!isLoading()){
            Restaurant recipe = new Restaurant();
            recipe.setName("LOADING...");
            List<Restaurant> loadingList = new ArrayList<>();
            loadingList.add(recipe);
            allRestaurants = loadingList;
            notifyDataSetChanged();
        }
    }

    private boolean isLoading(){
        if (allRestaurants != null){
            if (allRestaurants.size() > 0){
                return allRestaurants.get(allRestaurants.size() - 1).getName().equals("LOADING...");
            }
        }
        return false;
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
