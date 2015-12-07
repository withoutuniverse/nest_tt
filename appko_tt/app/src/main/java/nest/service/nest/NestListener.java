package nest.service.nest;

public interface NestListener {

    void onInitiateLogin();

    void onAuthStatusChanged(boolean isAuth);

    void onLoggedOut();

    void onLoggedIn();
}
