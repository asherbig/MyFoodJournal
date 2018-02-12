package team7202.myfoodjournal;

/**
 * Created by Zach on 1/31/2018.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

/**
 * Fragment that appears in the "content_frame", shows the relevant page of the application
 */
public class PageFragment extends Fragment {
    public static final String ARG_MENU_OPTION = "menu_option";

    public PageFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String layout = getArguments().getString(ARG_MENU_OPTION);
        int resourceIdentifier = getResourceId(layout, R.layout.class);
        return inflater.inflate(resourceIdentifier, container, false);
    }

    private int getResourceId(String resourceName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resourceName);
            return idField.getInt(idField);
        } catch (Exception e) {
            throw new RuntimeException("No resource ID found for: "
                    + resourceName + " / " + c, e);
        }
    }
}