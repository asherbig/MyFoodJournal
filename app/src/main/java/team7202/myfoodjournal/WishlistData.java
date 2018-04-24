package team7202.myfoodjournal;

/**
 * Created by Zach on 4/1/2018.
 */

public class WishlistData {
    public String reviewId;
    public String restaurant_name;
    public String restaurant_id;
    public String address;
    public String menuitem;
    public String date_submitted;

    public WishlistData(String reviewId, String restaurant_name, String restaurant_id, String address, String menuitem, String date_submitted) {
        this.reviewId = reviewId;
        this.restaurant_name = restaurant_name;
        this.restaurant_id = restaurant_id;
        this.address = address;
        this.menuitem = menuitem;
        this.date_submitted = date_submitted;
    }
}