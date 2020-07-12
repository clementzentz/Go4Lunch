package clement.zentz.go4lunch.ui.workmates;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.models.workmate.Workmate;
import de.hdodenhof.circleimageview.CircleImageView;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.WorkmateViewHolder> {

    private List<Workmate> mWorkmateList;

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workmates_cardview, parent, false);
        return new WorkmatesAdapter.WorkmateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position) {

        holder.workmateTxt.setText(mWorkmateList.get(position).getWorkmateName()+" is eating at "+mWorkmateList.get(position).getRestaurantId());
    }

    @Override
    public int getItemCount() {
        if (mWorkmateList != null){
            return mWorkmateList.size();
        }
        return 0;
    }

    public void setWorkmateList(List<Workmate> workmates){
        mWorkmateList = workmates;
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
