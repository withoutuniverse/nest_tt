package nest.activity;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import nest.R;
import nest.fragment.AuthFragment;
import nest.service.nest.NestListener;

public abstract class BaseActivity extends AppCompatActivity implements NestListener {

    @Override
    public void onLoggedOut() {
        finish();
    }

    @Override
    public void onInitiateLogin() {
        showAuth();
    }

    @Override
    public void onAuthStatusChanged(boolean isAuth) {
        if (!isAuth) {
            onLoggedOut();
        }
    }

    protected void showAuth() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(android.R.id.content, new AuthFragment(), AuthFragment.TAG).commit();
    }
}
