package nest.service.nest;

import nest.model.EmptyBody;
import nest.model.AccessToken;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface NestApi {

    String FIREBASE_NEST_URL = "https://developer-api.nest.com";
    String REQUEST_CLIENT_CODE_URL = "https://home.nest.com/login/oauth2?client_id=%s&state=%s";
    String ACCESS_TOKEN_ENDPOINT = "https://api.home.nest.com/oauth2/";

    /**
     * Use with {@link NestApi#ACCESS_TOKEN_ENDPOINT}
     */
    @POST("/access_token?grant_type=authorization_code")
    @Headers("Content-type: application/x-www-form-urlencoded; charset=UTF-8")
    Observable<AccessToken> getAccessToken(
            @Query("code") String code,
            @Query("client_id") String clientId,
            @Query("client_secret") String clientSecret,
            @Body EmptyBody body
    );
}
