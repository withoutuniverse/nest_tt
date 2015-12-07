package nest.model;

import android.support.annotation.StringDef;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@JsonObject
public class SmokeCoAlarms extends Device {

    public static final String SMOKE_AND_CO_ALARMS = "smoke_co_alarms";

    public static final String FIELD_SMOKE_ALARM_STATE = "smoke_alarm_state";
    public static final String FIELD_UI_COLOR_STATE = "ui_color_state";
    public static final String FIELD_BATTERY_HEALTH = "battery_health";
    public static final String FIELD_LOCALE = "locale";
    public static final String FIELD_IS_ONLINE = "is_online";
    public static final String FIELD_LAST_MANUAL_TEST_TIME = "last_manual_test_time";
    public static final String FIELD_DEVICE_ID = "device_id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_STRUCTURE_ID = "structure_id";
    public static final String FIELD_WHERE_ID = "where_id";
    public static final String FIELD_SOFTWARE_VERSION = "software_version";
    public static final String FIELD_NAME_LONG = "name_long";
    public static final String FIELD_IS_MANUAL_TEST_ACTIVE = "is_manual_test_active";
    public static final String FIELD_CO_ALARM_STATE = "co_alarm_state";

    @StringDef({FIELD_SMOKE_ALARM_STATE, FIELD_UI_COLOR_STATE, FIELD_BATTERY_HEALTH, FIELD_LOCALE, FIELD_IS_ONLINE,
            FIELD_LAST_MANUAL_TEST_TIME, FIELD_DEVICE_ID, FIELD_NAME, FIELD_STRUCTURE_ID, FIELD_WHERE_ID,
            FIELD_SOFTWARE_VERSION, FIELD_NAME_LONG, FIELD_IS_MANUAL_TEST_ACTIVE, FIELD_CO_ALARM_STATE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SmokeCoAlarmsField {
    }

    @JsonField(name = FIELD_DEVICE_ID)
    public String deviceId;

    @JsonField(name = FIELD_LOCALE)
    public String locale;

    @JsonField(name = FIELD_SOFTWARE_VERSION)
    public String softwareVersion;

    @JsonField(name = FIELD_STRUCTURE_ID)
    public String structureId;

    @JsonField(name = FIELD_NAME)
    public String name;

    @JsonField(name = FIELD_NAME_LONG)
    public String nameLong;

    @JsonField(name = FIELD_LAST_CONNECTION)
    public String lastConnection;

    @JsonField(name = FIELD_IS_ONLINE)
    public boolean isOnline;

    @JsonField(name = FIELD_BATTERY_HEALTH)
    @BatteryState.BatteryStateField
    public String batteryHealth;

    @JsonField(name = FIELD_CO_ALARM_STATE)
    @AlarmState.AlarmStateField
    public String coAlarmState;

    @JsonField(name = FIELD_SMOKE_ALARM_STATE)
    @AlarmState.AlarmStateField
    public String smokeAlarmState;

    @JsonField(name = FIELD_IS_MANUAL_TEST_ACTIVE)
    public boolean isManualTestActive;

    @JsonField(name = FIELD_LAST_MANUAL_TEST_TIME)
    public String lastManualTestTime;

    @JsonField(name = FIELD_UI_COLOR_STATE)
    @UiColorState.UiColorStateField
    public String uiColorState;

    @JsonField(name = FIELD_WHERE_ID)
    public String whereId;

    public SmokeCoAlarms() {
    }

    @Override
    public String getPath(@SmokeCoAlarmsField String field) {
        return super.getPath(field);
    }

    @Override
    protected String getDeviceName() {
        return SMOKE_AND_CO_ALARMS;
    }
}
