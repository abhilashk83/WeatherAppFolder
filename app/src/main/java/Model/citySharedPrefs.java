package Model;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by abhilashk on 11/19/2017.
 */

public class citySharedPrefs {

    SharedPreferences sp;

    public citySharedPrefs(Activity activity) {
        sp = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    public String getCity() {
        return sp.getString("city", "Alpharetta,US");
    }

    public void setCity(String city) {
        sp.edit().putString("city", city).commit();
    }
}
