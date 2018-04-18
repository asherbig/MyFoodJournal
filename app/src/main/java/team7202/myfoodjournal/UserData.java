package team7202.myfoodjournal;

/**
 * Created by Zach on 4/11/2018.
 */

public class UserData {
    public String username;
    public String email;
    public String firstname;
    public String lastname;
    public boolean isPublic;

    public UserData(String username, String email, String firstname, String lastname) {
        this.username = username;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.isPublic = true;
    }
}
