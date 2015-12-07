package nest.dagger;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nest.App;
import nest.service.UserPreferences;
import nest.service.nest.NestApi;
import nest.service.nest.NestApiAdapter;
import nest.service.nest.NestManager;

@Module
public class MainModule {

    private final App app;

    public MainModule(App application) {
        app = application;
    }

    @Provides
    App provideApp() {
        return app;
    }

    @Provides
    Context provideContext() {
        return app.getApplicationContext();
    }

    @Provides
    OkHttpClient provideHttpClient() {
        return new OkHttpClient();
    }

    @Provides
    @Singleton
    NestApi provideNestApi() {
        return new NestApiAdapter();
    }

    @Provides
    @Singleton
    UserPreferences provideUserPreferences() {
        return new UserPreferences(app);
    }

    @Provides
    @Singleton
    NestManager provideNestManager() {
        return new NestManager();
    }
}
