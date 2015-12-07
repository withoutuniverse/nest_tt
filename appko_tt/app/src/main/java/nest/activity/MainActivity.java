package nest.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import nest.R;
import nest.fragment.MainFragment;
import nest.service.nest.NestListener;

public class MainActivity extends BaseActivity implements NestListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            showMain();
        }
    }

    @Override
    public void onLoggedIn() {
        showMain();
    }

    private void showMain() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(android.R.id.content, new MainFragment(), MainFragment.TAG).commit();
    }

}
