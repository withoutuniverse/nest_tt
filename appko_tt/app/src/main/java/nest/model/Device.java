package nest.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class Device {

    public static final String DEVICES = "devices";

    public static final String FIELD_DEVICE_ID = "device_id";
    public static final String FIELD_LOCALE = "locale";
    public static final String FIELD_SOFTWARE_VERSION = "software_version";
    public static final String FIELD_STRUCTURE_ID = "structure_id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_NAME_LONG = "name_long";
    public static final String FIELD_LAST_CONNECTION = "last_connection";
    public static final String FIELD_IS_ONLINE = "is_online";

    @JsonField(name = FIELD_DEVICE_ID)
    public String deviceID;

    @JsonField(name = FIELD_LOCALE)
    public String locale;

    @JsonField(name = FIELD_SOFTWARE_VERSION)
    public String softwareVersion;

    @JsonField(name = FIELD_STRUCTURE_ID)
    public String structureID;

    @JsonField(name = FIELD_NAME)
    public String name;

    @JsonField(name = FIELD_NAME_LONG)
    public String nameLong;

    @JsonField(name = FIELD_LAST_CONNECTION)
    public String lastConnection;

    @JsonField(name = FIELD_IS_ONLINE)
    public boolean isOnline;

    public String getPath(String field) {
        return "/" + DEVICES + "/" + getDeviceName() + "/" + deviceID + "/" + field;
    }

    protected String getDeviceName() {
        return null;
    }
}
