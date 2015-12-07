package nest.service.nest;

import com.squareup.okhttp.OkHttpClient;

import nest.model.EmptyBody;
import nest.model.AccessToken;
import nest.retrofit.JsonConverter;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import rx.Observable;

public class NestApiAdapter implements NestApi {

    public NestApiAdapter() {
    }

    @Override
    public Observable<AccessToken> getAccessToken(String code, String clientId, String clientSecret, EmptyBody body) {
        return getHostAdapter(NestApi.ACCESS_TOKEN_ENDPOINT)
                .create(NestApi.class)
                .getAccessToken(code, clientId, clientSecret, body);
    }

    private RestAdapter getHostAdapter(String host){
        return new RestAdapter.Builder()
                .setEndpoint(host)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setClient(new OkClient(new OkHttpClient()))
                .setConverter(new JsonConverter())
                .build();
    }
}
