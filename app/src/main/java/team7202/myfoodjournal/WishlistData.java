package team7202.myfoodjournal;

/**
 * Created by Zach on 4/1/2018.
 */

public class WishlistData {
    public String restaurant_name;
    public String address;
    public String restaurant_id;
    public String menuitem;
    public String date_submitted;

    public WishlistData(String restaurant_name, String restaurant_id, String address, String menuitem, String date_submitted) {
        this.restaurant_name = restaurant_name;
        this.address = address;
        this.restaurant_id = restaurant_id;
        this.menuitem = menuitem;
        this.date_submitted = date_submitted;
    }
}