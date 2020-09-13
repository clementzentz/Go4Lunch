package clement.zentz.go4lunch.models.restaurantsAndWorkmates;

import java.util.ArrayList;
import java.util.List;

import clement.zentz.go4lunch.models.restaurant.Restaurant;
import clement.zentz.go4lunch.models.workmate.Workmate;

public class RestaurantsAndWorkmates {

    private List<Workmate> mWorkmates;
    private List<Restaurant> mRestaurants;

    public RestaurantsAndWorkmates(List<Workmate> workmates, List<Restaurant> restaurants) {
        mWorkmates = workmates;
        mRestaurants = restaurants;
    }

    public List<Workmate> getWorkmates() {
        return mWorkmates;
    }

    public void setWorkmates(List<Workmate> workmates) {
        mWorkmates = workmates;
    }

    public List<Restaurant> getRestaurants() {
        return mRestaurants;
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        mRestaurants = restaurants;
    }
}
