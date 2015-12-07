package nest.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class Eta {

    @JsonField(name = "trip_id")
    public String tripID;

    @JsonField(name = "estimated_arrival_window_begin")
    public String estimatedArrivalWindowBegin;

    @JsonField(name = "estimated_arrival_window_end")
    public String estimatedArrivalWindowEnd;
}
