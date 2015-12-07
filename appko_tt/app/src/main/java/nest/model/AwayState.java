package nest.model;


import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AwayState {

    public static final String FIELD_AWAY = "away";
    public static final String FIELD_AUTO_AWAY = "auto-away";
    public static final String FIELD_HOME = "home";

    @StringDef({FIELD_AWAY, FIELD_AUTO_AWAY, FIELD_HOME})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AwayStateField {
    }
}
