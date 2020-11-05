package clement.zentz.go4lunch.util;

import com.google.firebase.Timestamp;

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
}
