package team7202.myfoodjournal;

/**
 * Created by Zach on 2/2/2018.
 */

public class UsernameSingleton {
    private static UsernameSingleton instance;

    public static UsernameSingleton getInstance() {
        if (instance == null) {
            instance = new UsernameSingleton();
        }
        return instance;
    }

    private UsernameSingleton() {

    }

    private String username;
    private boolean profileVisibility;

    public boolean getVisibility() { return profileVisibility; }

    public void setVisibility(boolean visibility) { this.profileVisibility = visibility; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}