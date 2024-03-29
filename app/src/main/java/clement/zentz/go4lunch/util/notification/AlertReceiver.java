package clement.zentz.go4lunch.util.notification;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import clement.zentz.go4lunch.R;
import clement.zentz.go4lunch.ui.DetailActivity;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.util.Constants;
import clement.zentz.go4lunch.util.convert.ConvertUtil;

import static clement.zentz.go4lunch.util.Constants.CHANNEL_ID;
import static clement.zentz.go4lunch.util.Constants.notificationId;

public class AlertReceiver extends BroadcastReceiver {

    private Workmate currentUser;
    private final List<Workmate> workmatesJoining = new ArrayList<>();
    private String workmatesJoiningNames;

    @Override
    public void onReceive(Context context, Intent intent) {

        ConvertUtil convertUtil = new ConvertUtil();

        if (intent.hasExtra(Constants.INTENT_CURRENT_RESTAURANT_ID) && intent.hasExtra(Constants.INTENT_CURRENT_USER_ID)){
            String userId = intent.getStringExtra(Constants.INTENT_CURRENT_USER_ID);
            String restaurantId = intent.getStringExtra(Constants.INTENT_CURRENT_RESTAURANT_ID);

            Task<DocumentSnapshot> currentUserTask = FirebaseFirestore.getInstance().collection(Constants.WORKMATES_COLLECTION).document(userId).get();
            Task<QuerySnapshot> workmatesJoiningTask = FirebaseFirestore.getInstance().collection(Constants.WORKMATES_COLLECTION).whereEqualTo(Constants.RESTAURANT_ID, restaurantId).get();

            currentUserTask.addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.getData() != null){
                    currentUser = convertUtil.convertMapToWorkmate(documentSnapshot.getData());
                }
            });

            workmatesJoiningTask.addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                    workmatesJoining.add(convertUtil.convertMapToWorkmate(queryDocumentSnapshot.getData()));
                    for (Workmate workmate : workmatesJoining){
                        if (workmatesJoiningNames != null){
                            workmatesJoiningNames = workmatesJoiningNames +" "+ workmate.getWorkmateName();
                        }else {
                            workmatesJoiningNames = workmate.getWorkmateName();
                        }
                    }
                }
            });

            Tasks.whenAll(workmatesJoiningTask, currentUserTask).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Create an explicit intent for an Activity in your app
                    Intent intentNotification = new Intent(context, DetailActivity.class);
                    intentNotification.putExtra(Constants.INTENT_CURRENT_USER_ID, userId);
                    intentNotification.putExtra(Constants.INTENT_CURRENT_RESTAURANT_ID, restaurantId);
                    intentNotification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentNotification, 0);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_baseline_restaurant_24)
                            .setContentTitle(Constants.APP_NAME)
                            .setContentText("notification reminder : ")
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(
                                    "Hey ! you have a lunch at "
//                                            + currentUser.getRestaurantName()+" "
//                                            + currentUser.getRestaurantAddress()+". " //get type + get Vicinity
                                            +" With your fellow workmates : "
                                            + workmatesJoiningNames
                            ))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setCategory(NotificationCompat.CATEGORY_REMINDER)
                            .setContentIntent(pendingIntent);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    // notificationId is a unique int for each notification that you must define
                    notificationManager.notify(notificationId, builder.build());
                }
            });
        }
    }
}
