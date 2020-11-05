package clement.zentz.go4lunch.models.rating;

public class GlobalRating {

    private double globalRating;
    private String restaurantId;

    public GlobalRating(double globalRating, String restaurantId) {
        this.restaurantId = restaurantId;
        this.globalRating = globalRating;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public double getGlobalRating() {
        return globalRating;
    }

    public void setGlobalRating(double globalRating) {
        this.globalRating = globalRating;
    }
}
