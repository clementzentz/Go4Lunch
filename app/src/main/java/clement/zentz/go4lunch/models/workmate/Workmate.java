package clement.zentz.go4lunch.models.workmate;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.Timestamp;
import java.util.List;
import java.util.Objects;

import clement.zentz.go4lunch.models.restaurant.Restaurant;

public class Workmate implements Parcelable {

    private String workmateId;
    private String workmateName;
    private String email;
    private String photoUrl;
    //the id of the restaurant associated with the workmate
    private String restaurantId;
    private Timestamp mTimestamp;
    private Restaurant mRestaurant;

    public Workmate(String workmateId, String workmateName, String email, String photoUrl, String restaurantId, Timestamp timestamp) {
        this.workmateId = workmateId;
        this.workmateName = workmateName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.restaurantId = restaurantId;
        mRestaurant = null;
        mTimestamp = timestamp;
    }

    public Workmate(Workmate workmate){
        workmateId = workmate.getWorkmateId();
        workmateName = workmate.getWorkmateName();
        email = workmate.getEmail();
        photoUrl = workmate.getPhotoUrl();
        restaurantId = workmate.getRestaurantId();
        mTimestamp = workmate.getTimestamp();
    }

    protected Workmate(Parcel in) {
        workmateId = in.readString();
        workmateName = in.readString();
        email = in.readString();
        photoUrl = in.readString();
        restaurantId = in.readString();
        mTimestamp = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public static final Creator<Workmate> CREATOR = new Creator<Workmate>() {
        @Override
        public Workmate createFromParcel(Parcel in) {
            return new Workmate(in);
        }

        @Override
        public Workmate[] newArray(int size) {
            return new Workmate[size];
        }
    };

    public String getWorkmateId() {
        return workmateId;
    }

    public void setWorkmateId(String workmateId) {
        this.workmateId = workmateId;
    }

    public String getWorkmateName() {
        return workmateName;
    }

    public void setWorkmateName(String workmateName) {
        this.workmateName = workmateName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Timestamp getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        mTimestamp = timestamp;
    }

    public Restaurant getRestaurant(){
        return mRestaurant;
    }

    public void setRestaurant(Restaurant restaurant){
        mRestaurant = restaurant;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(workmateId);
        parcel.writeString(workmateName);
        parcel.writeString(email);
        parcel.writeString(photoUrl);
        parcel.writeString(restaurantId);
        parcel.writeParcelable(mTimestamp, i);
    }

    @Override
    public String toString() {
        return "Workmate{" +
                "workmateId='" + workmateId + '\'' +
                ", workmateName='" + workmateName + '\'' +
                ", email='" + email + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", restaurantId='" + restaurantId + '\'' +
                ", mTimestamp=" + mTimestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workmate workmate = (Workmate) o;
        return Objects.equals(workmateId, workmate.workmateId) &&
                Objects.equals(workmateName, workmate.workmateName) &&
                Objects.equals(email, workmate.email) &&
                Objects.equals(photoUrl, workmate.photoUrl) &&
                Objects.equals(restaurantId, workmate.restaurantId) &&
                Objects.equals(mTimestamp, workmate.mTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workmateId, workmateName, email, photoUrl, restaurantId, mTimestamp);
    }
}
