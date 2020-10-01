package clement.zentz.go4lunch.util.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import clement.zentz.go4lunch.R;

public class RatingBarDialogFragment extends DialogFragment {

    private static final String TAG = "RatingBarDialogFragment";

    private RatingBarDialogListener mListener;

    public interface RatingBarDialogListener {
        void onDialogPositiveClick(float rating);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.custom_rating_dialog, null);
        RatingBar ratingBar = linearLayout.findViewById(R.id.rating_bar_dialog);

        builder.setTitle("Noter ce restaurant :")
                .setView(linearLayout)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogPositiveClick(ratingBar.getRating());
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "onClick: no rating value input.");
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (RatingBarDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString()
                    + " must implement NoticeDialogListener");

        }
    }
}
