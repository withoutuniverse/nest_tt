package nest.dagger;

import javax.inject.Singleton;

import dagger.Component;
import nest.App;
import nest.fragment.AuthFragment;
import nest.fragment.MainFragment;
import nest.service.nest.NestManager;

@Singleton
@Component(modules = {MainModule.class})
public interface Graph {

    // App
    void inject(App app);

    // Fragment
    void inject(MainFragment fragment);
    void inject(AuthFragment fragment);

    // Service
    void inject(NestManager manager);
}