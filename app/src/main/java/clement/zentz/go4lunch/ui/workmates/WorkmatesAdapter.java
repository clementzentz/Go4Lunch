package clement.zentz.go4lunch.ui.workmates;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import clement.zentz.go4lunch.R;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.WorkmateViewHolder> {

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workmates_cardview, parent, false);
        return new WorkmatesAdapter.WorkmateViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    static class WorkmateViewHolder extends RecyclerView.ViewHolder{

        public WorkmateViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
