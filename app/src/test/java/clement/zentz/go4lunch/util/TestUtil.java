package clement.zentz.go4lunch.util;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import clement.zentz.go4lunch.models.rating.GlobalRating;
import clement.zentz.go4lunch.models.rating.Rating;
import clement.zentz.go4lunch.models.workmate.Workmate;

public class TestUtil {

    public static final Workmate TEST_WORKMATE_1 = new Workmate(
            "246Zs29vGeF545X6gDPaUWxbtf2L",
            "Joe Biden",
            "JoeBiden@gmail.com",
            "",
            "23qz7jMyF75GKczRR3tJ5REf3j4",
            "Burger King",
            "18 Chiltern St, Marylebone, London W1U 7QA, Royaume-Uni",
            Timestamp.now());

    public static final Workmate TEST_WORKMATE_2 = new Workmate(
            "77KZS2Dz8cjR98p2RecpYE7g5dJ5",
            "Donald Trump",
            "DonaldTrump@outlook.com",
            "",
            "BTtd68RDLij8UjM6B2tZi3gi824",
            "McDonald",
            "194 Kensington Park Rd, London W11 2ES, Royaume-Uni",
            Timestamp.now());

    public static final List<Workmate> TEST_WORKMATE_LIST = new ArrayList<>(Arrays.asList(TEST_WORKMATE_1, TEST_WORKMATE_2));

    public static final Rating TEST_RATING_1 = new Rating(0, "23qz7jMyF75GKczRR3tJ5REf3j4", "246Zs29vGeF545X6gDPaUWxbtf2L");
    public static final Rating TEST_RATING_2 = new Rating(1, "23qz7jMyF75GKczRR3tJ5REf3j4", "77KZS2Dz8cjR98p2RecpYE7g5dJ5");

    public static final Rating TEST_RATING_3 = new Rating(2, "BTtd68RDLij8UjM6B2tZi3gi824", "246Zs29vGeF545X6gDPaUWxbtf2L");
    public static final Rating TEST_RATING_4 = new Rating(3, "BTtd68RDLij8UjM6B2tZi3gi824", "77KZS2Dz8cjR98p2RecpYE7g5dJ5");

    public static final List<Rating> TEST_RATING_LIST_4_THIS_RESTAURANT = new ArrayList<>(Arrays.asList(TEST_RATING_1, TEST_RATING_2));

    public static final GlobalRating TEST_GLOBAL_RATING_1 = new GlobalRating(0.5, "23qz7jMyF75GKczRR3tJ5REf3j4");
    public static final GlobalRating TEST_GLOBAL_RATING_2 = new GlobalRating(2.5, "BTtd68RDLij8UjM6B2tZi3gi824");

    public static final List<GlobalRating> TEST_GLOBAL_RATING_LIST = new ArrayList<>(Arrays.asList(TEST_GLOBAL_RATING_1, TEST_GLOBAL_RATING_2));
}
