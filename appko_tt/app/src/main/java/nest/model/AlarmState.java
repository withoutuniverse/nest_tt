package nest.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AlarmState {

    public static final String FIELD_OK = "ok";
    public static final String FIELD_WARNING = "warning";
    public static final String FIELD_EMERGENCY = "emergency";

    @StringDef({FIELD_OK, FIELD_WARNING, FIELD_EMERGENCY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AlarmStateField {
    }


}
