package team7202.myfoodjournal;

/**
 * Created by abhaydalmia on 2/15/18.
 */

public class ReviewData {
    public String reviewId;
    public String userId;
    public String restaurant_id;
    public String restaurant_name;
    public String menuitem;
    public int rating;
    public String description;
    public String date_submitted;
    public String address;

    public ReviewData(String reviewId, String userId, String restaurant_id, String restaurant_name,
                      String menuitem, int rating, String description, String date_submitted,
                      String address) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.restaurant_id = restaurant_id;
        this.restaurant_name = restaurant_name;
        this.menuitem = menuitem;
        this.rating = rating;
        this.description = description;
        this.date_submitted = date_submitted;
        this.address = address;
    }
}
