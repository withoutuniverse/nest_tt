package nest.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import javax.inject.Inject;

import nest.App;
import nest.R;
import nest.model.AccessToken;
import nest.model.EmptyBody;
import nest.service.nest.NestApi;
import nest.service.nest.NestManager;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class AuthFragment extends BaseFragment {

    public static final String TAG = AuthFragment.class.getSimpleName();

    @Nullable
    private static String parseCode(String url) {
        try {
            return Uri.parse(url).getQueryParameter("code");
        } catch (Exception e) {
            return null;
        }
    }

    @Inject
    NestManager nestManager;

    @Inject
    NestApi nestApi;

    private String code;

    private ViewHolder viewHolder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        App.getGraph().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewHolder = new ViewHolder(inflater.inflate(R.layout.fragment_auth, container, false));
        return viewHolder.root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewHolder = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        showProgress();
        showLoginPage();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void showLoginPage() {
        viewHolder.webView.setWebChromeClient(new ProgressChromeClient());
        viewHolder.webView.setWebViewClient(new RedirectClient());
        viewHolder.webView.getSettings().setJavaScriptEnabled(true);

        final String url = String.format(NestApi.REQUEST_CLIENT_CODE_URL, nestManager.getMetadata().getClientID(), nestManager.getMetadata().getStateValue());
        Timber.v("Loading url: " + url);
        viewHolder.webView.loadUrl(url);
    }

    private void requestAccessToken() {
        showProgress();
        nestApi.getAccessToken(code, nestManager.getMetadata().getClientID(), nestManager.getMetadata().getClientSecret(), new EmptyBody())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<AccessToken>() {
                    @Override
                    public void call(AccessToken accessToken) {
                        onAccessTokenCompleted(accessToken);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        onAccessTokenFailed(throwable);
                    }
                });
    }

    private void onAccessTokenCompleted(AccessToken token) {
        Timber.v("Got access token: %s", token);
        if (token != null) {
            nestManager.onAuthenticated(token);
            nestListener.onLoggedIn();
        } else {
            onAccessTokenFailed(null);
        }
    }

    private void onAccessTokenFailed(Throwable t) {
        Timber.e("Unable to get access token: %s", t);
    }

    private class ViewHolder {

        public final View root;
        public final WebView webView;

        private ViewHolder(View root) {
            this.root = root;
            webView = (WebView) root.findViewById(R.id.activity_webview);
        }
    }

    private class ProgressChromeClient extends WebChromeClient {

        private boolean visible = true;

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress < 100 && !visible) {
                visible = true;
                showProgress();
            } else if (newProgress == 100 && visible) {
                visible = false;
                hideProgress();
            }
        }
    }

    private class RedirectClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!url.startsWith(nestManager.getMetadata().getRedirectURL())) {
                code = null;
                return false;
            }
            code = parseCode(url);
            Timber.v("Got code: %s", code);
            if (TextUtils.isEmpty(code)) {
                return true;
            }
            requestAccessToken();
            return true;
        }
    }
}
