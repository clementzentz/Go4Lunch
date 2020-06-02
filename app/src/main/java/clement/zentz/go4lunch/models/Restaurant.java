package clement.zentz.go4lunch.models;

import android.widget.ImageView;

public class Restaurant {

    private String restaurantName;
    private String restaurantType;
    private String restaurantAddress;
    private String restaurantClock;
    private String restaurantDistance;
    private ImageView restaurantImg;
    private int restaurantStars, workmateNumber;

    public Restaurant(String restaurantName, String restaurantType, String restaurantAddress, String restaurantClock, String restaurantDistance, ImageView restaurantImg, int restaurantStars, int workmateNumber) {
        this.restaurantName = restaurantName;
        this.restaurantType = restaurantType;
        this.restaurantAddress = restaurantAddress;
        this.restaurantClock = restaurantClock;
        this.restaurantDistance = restaurantDistance;
        this.restaurantImg = restaurantImg;
        this.restaurantStars = restaurantStars;
        this.workmateNumber = workmateNumber;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantType() {
        return restaurantType;
    }

    public void setRestaurantType(String restaurantType) {
        this.restaurantType = restaurantType;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public String getRestaurantClock() {
        return restaurantClock;
    }

    public void setRestaurantClock(String restaurantClock) {
        this.restaurantClock = restaurantClock;
    }

    public String getRestaurantDistance() {
        return restaurantDistance;
    }

    public void setRestaurantDistance(String restaurantDistance) {
        this.restaurantDistance = restaurantDistance;
    }

    public ImageView getRestaurantImg() {
        return restaurantImg;
    }

    public void setRestaurantImg(ImageView restaurantImg) {
        this.restaurantImg = restaurantImg;
    }

    public int getRestaurantStars() {
        return restaurantStars;
    }

    public void setRestaurantStars(int restaurantStars) {
        this.restaurantStars = restaurantStars;
    }

    public int getWorkmateNumber() {
        return workmateNumber;
    }

    public void setWorkmateNumber(int workmateNumber) {
        this.workmateNumber = workmateNumber;
    }
}
