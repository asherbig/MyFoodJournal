package team7202.myfoodjournal;

/**
 * Created by abhaydalmia on 2/15/18.
 */

public class ReviewData {
    public String restaurant_name;
    public String menuitem;
    public int rating;
    public String description;
    public ReviewData(String restaurant_name, String menuitem, int rating, String description) {
        this.restaurant_name = restaurant_name;
        this.menuitem = menuitem;
        this.rating = rating;
        this.description = description;
    }
}
