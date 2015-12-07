package nest.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class AccessToken implements Parcelable {

    @JsonField(name = "access_token")
    public String token;

    @JsonField(name = "expires_in")
    public long expiresIn;

    public AccessToken() {
    }

    public AccessToken(String token, long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }

    private AccessToken(Parcel in) {
        token = in.readString();
        expiresIn = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(token);
        dest.writeLong(expiresIn);
    }

    public static final Parcelable.Creator<AccessToken> CREATOR = new Parcelable.Creator<AccessToken>() {

        @Override
        public AccessToken createFromParcel(Parcel source) {
            return new AccessToken(source);
        }

        @Override
        public AccessToken[] newArray(int size) {
            return new AccessToken[size];
        }
    };
}
