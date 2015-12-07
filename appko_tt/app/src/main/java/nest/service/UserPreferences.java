package nest.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import nest.model.AccessToken;

public class UserPreferences {

    private static final String PREF_KEY = "token.key";
    private static final String PREF_EXPIRATION = "token.expiration";

    private final SharedPreferences prefs;

    public UserPreferences(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public AccessToken getToken() {
        final String token = prefs.getString(PREF_KEY, null);
        final long expirationDate = prefs.getLong(PREF_EXPIRATION, -1);

        if (token == null || expirationDate == -1) {
            return null;
        }

        return new AccessToken(token, expirationDate);
    }

    public void setNestToken(AccessToken token) {
        if (token == null) {
            prefs.edit()
                    .remove(PREF_KEY)
                    .remove(PREF_EXPIRATION)
                    .apply();
        } else {
            prefs.edit()
                    .putString(PREF_KEY, token.token)
                    .putLong(PREF_EXPIRATION, token.expiresIn)
                    .apply();
        }
    }
}
