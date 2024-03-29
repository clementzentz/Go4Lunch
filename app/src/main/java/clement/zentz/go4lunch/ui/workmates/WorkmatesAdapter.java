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
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.ui.listRestaurant.ListInterfaceToAdapter;
import de.hdodenhof.circleimageview.CircleImageView;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.WorkmateViewHolder> {

    private final List<Workmate> allWorkmates = new ArrayList<>();

    private final boolean isDetailActivity;

    public ListInterfaceToAdapter mListInterfaceToAdapter;


    public WorkmatesAdapter(boolean isDetailActivity){
        this.isDetailActivity = isDetailActivity;
    }

    public WorkmatesAdapter(boolean isDetailActivity, ListInterfaceToAdapter listInterfaceToAdapter){
        this.isDetailActivity = isDetailActivity;
        mListInterfaceToAdapter = listInterfaceToAdapter;
    }

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workmates_cardview, parent, false);
        return new WorkmatesAdapter.WorkmateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position) {

        if (allWorkmates.get(position).getPhotoUrl() != null){
            if (!allWorkmates.get(position).getPhotoUrl().isEmpty()){
                Picasso.get().load(allWorkmates.get(position).getPhotoUrl()).into(holder.workmateProfileImg);
            }
        }

        if (!isDetailActivity){
            for (Workmate workmate : allWorkmates) {
                if (workmate.getRestaurantId().equals(allWorkmates.get(position).getRestaurantId())){
                    if (allWorkmates.get(position).getWorkmateName() != null){
                        holder.workmateTxt.setText(allWorkmates.get(position).getWorkmateName()+ " is eating at " +workmate.getRestaurant().getName());
                    }else {
                        holder.workmateTxt.setText("No account name found is eating at " +workmate.getRestaurant().getName());
                    }
                }else if (allWorkmates.get(position).getRestaurantId().isEmpty()){
                    holder.workmateTxt.setText(allWorkmates.get(position).getWorkmateName()+ " hasn't decided yet...");
                }
            }
        }else {
            if (allWorkmates.get(position).getWorkmateName() != null){
                holder.workmateTxt.setText(allWorkmates.get(position).getWorkmateName()+ " is joining!");
            }else {
                holder.workmateTxt.setText("No account name found is joining!");
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(allWorkmates.get(position).getRestaurant() != null){
                    mListInterfaceToAdapter.launchDetailRestaurantActivity(allWorkmates.get(position).getRestaurant());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return allWorkmates.size();
    }

    public void setAllWorkmates(List<Workmate> workmates){
        allWorkmates.clear();
        allWorkmates.addAll(workmates);
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
