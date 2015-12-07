package nest.model;

import android.support.annotation.StringDef;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

@JsonObject
public class Structure {

    public static final String STRUCTURES = "structures";

    public static final String FIELD_STRUCTURE_ID = "structure_id";
    public static final String FIELD_THERMOSTATS = "thermostats";
    public static final String FIELD_SMOKE_CO_ALARMS = "smoke_co_alarms";
    public static final String FIELD_AWAY = "away";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_COUNTRY_CODE = "country_code";
    public static final String FIELD_PEAK_PERIOD_START_TIME = "peak_period_start_time";
    public static final String FIELD_PEAK_PERIOD_END_TIME = "peak_period_end_time";
    public static final String FIELD_TIME_ZONE = "time_zone";
    public static final String FIELD_ETA = "eta";

    @StringDef({FIELD_AWAY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface StructureField {
    }

    @JsonField(name = FIELD_STRUCTURE_ID)
    public String structureID;

    @JsonField(name = FIELD_THERMOSTATS)
    public List<String> thermostatIDs;

    @JsonField(name = FIELD_SMOKE_CO_ALARMS)
    public List<String> smokeCOAlarms;

    @JsonField(name = FIELD_NAME)
    public String name;

    @JsonField(name = FIELD_COUNTRY_CODE)
    public String countryCode;

    @JsonField(name = FIELD_PEAK_PERIOD_START_TIME)
    public String peakPeriodStartTime;

    @JsonField(name = FIELD_PEAK_PERIOD_END_TIME)
    public String peakPeriodEndTime;

    @JsonField(name = FIELD_TIME_ZONE)
    public String timeZone;

    @JsonField(name = FIELD_AWAY)
    @AwayState.AwayStateField
    public String awayState;

    @JsonField(name = FIELD_ETA)
    public Eta eta;

    public String getPath(@StructureField String field) {
        return "/" + STRUCTURES + "/" + structureID + "/" + field;
    }
}
