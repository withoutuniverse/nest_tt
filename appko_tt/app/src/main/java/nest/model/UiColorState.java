package nest.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class UiColorState {

    public static final String FIELD_GREEN = "green";
    public static final String FIELD_YELLOW = "yellow";
    public static final String FIELD_RED = "red";

    @StringDef({FIELD_GREEN, FIELD_YELLOW, FIELD_RED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface UiColorStateField {
    }

}
