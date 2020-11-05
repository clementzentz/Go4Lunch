package clement.zentz.go4lunch.models.mediatorsLiveData;

import java.util.List;

import clement.zentz.go4lunch.models.rating.GlobalRating;
import clement.zentz.go4lunch.models.workmate.Workmate;

public class WorkmatesAndGlobalRatings {

    private List<GlobalRating> mGlobalRatings;
    private List<Workmate> mWorkmateList;

    public WorkmatesAndGlobalRatings(List<GlobalRating> globalRatings, List<Workmate> workmateList) {
        mGlobalRatings = globalRatings;
        mWorkmateList = workmateList;
    }

    public List<GlobalRating> getGlobalRatings() {
        return mGlobalRatings;
    }

    public void setGlobalRatings(List<GlobalRating> globalRatings) {
        mGlobalRatings = globalRatings;
    }

    public List<Workmate> getWorkmateList() {
        return mWorkmateList;
    }

    public void setWorkmateList(List<Workmate> workmateList) {
        mWorkmateList = workmateList;
    }
}
