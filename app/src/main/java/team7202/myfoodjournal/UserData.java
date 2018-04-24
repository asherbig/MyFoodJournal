package team7202.myfoodjournal;

/**
 * Created by Zach on 4/11/2018.
 */

public class UserData {
    public String username;
    public String email;
    public String firstname;
    public String lastname;
    public String uid;
    public boolean isPublic;
    public boolean newFollowedReviewExists;

    public UserData(String username, String email, String firstname, String lastname, String uid) {
        this.username = username;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.uid = uid;
        this.isPublic = true;
        this.newFollowedReviewExists = false;
    }
}
