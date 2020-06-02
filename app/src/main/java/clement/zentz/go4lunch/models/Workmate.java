package clement.zentz.go4lunch.models;

public class Workmate {

    private String firstName;
    private String lastName;
    private String emailAddress;
    private String avatarImg;

    public Workmate(String firstName, String lastName, String emailAddress, String avatarImg) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.avatarImg = avatarImg;
    }
}
