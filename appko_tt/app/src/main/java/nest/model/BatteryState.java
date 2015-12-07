package nest.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class BatteryState {

    public static final String FIELD_OK = "ok";
    public static final String FIELD_REPLACE = "replace";

    @StringDef({FIELD_OK, FIELD_REPLACE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BatteryStateField {
    }


}
