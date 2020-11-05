package clement.zentz.go4lunch.models.rating;

public class Rating {

    private double rating;
    private String restaurantId;
    private String workmatesId;

    public Rating(double rating, String restaurantId, String workmatesId) {
        this.rating = rating;
        this.restaurantId = restaurantId;
        this.workmatesId = workmatesId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getWorkmatesId() {
        return workmatesId;
    }

    public void setWorkmatesId(String workmatesId) {
        this.workmatesId = workmatesId;
    }
}
