package clement.zentz.go4lunch.ui.workmates;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;
import de.hdodenhof.circleimageview.CircleImageView;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.WorkmateViewHolder> {

    private List<Workmate> mWorkmateList = new ArrayList<>();
    private List<Restaurant> mRestaurantList = new ArrayList<>();

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workmates_cardview, parent, false);
        return new WorkmatesAdapter.WorkmateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position) {

        if (!mWorkmateList.get(position).getPhotoUrl().isEmpty()){
            Picasso.get().load(mWorkmateList.get(position).getPhotoUrl()).into(holder.workmateProfileImg);
        }

        if (mRestaurantList!= null){
            for (Restaurant restaurant : mRestaurantList) {
                if (restaurant.getPlaceId().equals(mWorkmateList.get(position).getRestaurantId())){
                    holder.workmateTxt.setText(mWorkmateList.get(position).getWorkmateName()+" is eating at "+restaurant.getName());
                }else if (mWorkmateList.get(position).getRestaurantId().isEmpty()){
                    holder.workmateTxt.setText(mWorkmateList.get(position).getWorkmateName()+ " hasn't decided yet...");
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mWorkmateList != null){
            return mWorkmateList.size();
        }
        return 0;
    }

    public void setWorkmateList(List<Workmate> workmates){
        mWorkmateList.clear();
        mWorkmateList.addAll(workmates);
        notifyDataSetChanged();
    }

    public void setRestaurantList(List<Restaurant> restaurants){
        mRestaurantList.clear();
        mRestaurantList.addAll(restaurants);
        notifyDataSetChanged();
    }

    static class WorkmateViewHolder extends RecyclerView.ViewHolder{

        TextView workmateTxt;
        CircleImageView workmateProfileImg;

        public WorkmateViewHolder(@NonNull View itemView) {
            super(itemView);

            workmateTxt = itemView.findViewById(R.id.workmate_txt);
            workmateProfileImg = itemView.findViewById(R.id.workmate_img);
        }
    }
}
