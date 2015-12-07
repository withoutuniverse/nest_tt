package nest.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class MetaData {

    public static final String METADATA = "metadata";

    public static final String FIELD_ACCESS_TOKEN = "access_token";
    public static final String FIELD_CLIENT_VERSION = "client_version";

    @JsonField(name = FIELD_ACCESS_TOKEN)
    public String accessToken;

    @JsonField(name = FIELD_CLIENT_VERSION)
    public long clientVersion;
}
