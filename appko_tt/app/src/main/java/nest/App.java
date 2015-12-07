package nest;

import android.app.Application;

import nest.dagger.DaggerGraph;
import nest.dagger.Graph;
import nest.dagger.MainModule;
import nest.util.TraceTree;
import timber.log.Timber;

public class App extends Application {

    // no need in volatile, all usages in UI
    private static Graph daggerGraph;

    public static Graph getGraph() {
        return daggerGraph;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        daggerGraph = DaggerComponentInitializer.create(this);
        daggerGraph.inject(this);

        Timber.plant(new TraceTree(true));
    }

    private static final class DaggerComponentInitializer {

        public static Graph create(App app) {
            return DaggerGraph.builder().mainModule(new MainModule(app)).build();
        }

        private DaggerComponentInitializer() {
        }
    }
}
