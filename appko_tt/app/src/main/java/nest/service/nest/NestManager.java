package nest.service.nest;

import android.content.Context;
import android.support.annotation.WorkerThread;

import com.bluelinelabs.logansquare.LoganSquare;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;
import javax.inject.Provider;

import nest.App;
import nest.BuildConfig;
import nest.model.AccessToken;
import nest.model.ClientMetadata;
import nest.model.Device;
import nest.model.MetaData;
import nest.model.SmokeCoAlarms;
import nest.model.Structure;
import nest.service.UserPreferences;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

/**
 * Service to communicate with Nest backend
 */
@SuppressWarnings("unchecked") // for unchecked cast from Object to Map
public class NestManager implements ValueEventListener, Firebase.CompletionListener, Firebase.AuthListener {

    private static final Lock LOCK = new ReentrantLock();

    @Inject
    Context context;
    @Inject
    NestApi nestApi;
    @Inject
    Provider<UserPreferences> prefs;

    private final ClientMetadata metadata = new ClientMetadata(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET, BuildConfig.REDIRECT_URL);

    private WeakReference<NestListener> listenerRef;

    private final BehaviorSubject<MetaData> metadataSubject = BehaviorSubject.create();
    private final BehaviorSubject<Structure> structureSubject = BehaviorSubject.create();
    private final BehaviorSubject<SmokeCoAlarms> smokeCoAlarmsSubject = BehaviorSubject.create();

    private boolean authenticated = false;
    private boolean busy = false;
    private boolean waitingToken = false;

    private volatile Firebase firebase;

    public NestManager() {
        App.getGraph().inject(this);
    }

    /**
     * Authenticate on Nest backend. First of all, should obtain an access token.
     */
    private void login() {
        if (waitingToken || isAuthenticated()) {
            return;
        }
        final AccessToken token = prefs.get().getToken();
        if (token == null) {
            Timber.v("Token is empty");
            waitingToken = true;
            obtainAccessToken();
        } else {
            authenticate(token);
        }
    }

    public ClientMetadata getMetadata() {
        return metadata;
    }

    /**
     * Close and forget session
     */
    public void logout() {
        waitingToken = false;
        unsubscribeUpdates();
        if (isAuthenticated()) {
            getFirebase().unauth();
            setAuthenticated(false);
            prefs.get().setNestToken(null);
            final NestListener listener = getListener();
            if (listener != null) {
                listener.onLoggedOut();
            }
        }
    }

    /**
     * Start to startListen for updates from backend
     *
     * @param listener Listener should be able to help service to obtain access token
     */
    public void startListen(NestListener listener) {
        listenerRef = new WeakReference<>(listener);
        if (!busy && checkAuth()) {
            Timber.v("Starting...");
            subscribeUpdates();
        }
    }

    /**
     * Stop startListen to backend updates, if listener is used to startListen
     *
     * @param listener Listener should be the same that started listening
     */
    public void stopListen(NestListener listener) {
        if (isAuthenticated() && getListener() == listener) {
            Timber.v("Stopping...");
            listenerRef = null;
            unsubscribeUpdates();
        }
    }

    public void onAuthenticated(AccessToken token) {
        waitingToken = false;
        busy = true;
        Timber.v("Got auth token: " + token.token + " expires: " + token.expiresIn);
        prefs.get().setNestToken(token);
        authenticate(token);
    }


    @Override
    public void onDataChange(final DataSnapshot dataSnapshot) {
        Observable.create(
                new Observable.OnSubscribe<Boolean>() {
                    @Override
                    public void call(Subscriber<? super Boolean> subscriber) {
                        final Map<String, Object> values = dataSnapshot.getValue(StringObjectMapIndicator.INSTANCE);
                        for (Map.Entry<String, Object> entry : values.entrySet()) {
                            notifyUpdateListeners(entry);
                        }
                        subscriber.onNext(true);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.computation())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Timber.e(throwable, "Parse error");
                    }
                });
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        Timber.e("Cancelled: %s", firebaseError);
    }

    private boolean isAuthenticated() {
        return authenticated;
    }

    @SuppressWarnings("unused")
    public Observable<MetaData> getMetaDataObservable() {
        return metadataSubject;
    }

    public Observable<Structure> getStructureObservable() {
        return structureSubject;
    }

    public Observable<SmokeCoAlarms> getSmokeCoAlarmsObservable() {
        return smokeCoAlarmsSubject;
    }

    @WorkerThread
    private void notifyUpdateListeners(Map.Entry<String, Object> entry) {
        final Map<String, Object> value = (Map<String, Object>) entry.getValue();
        switch (entry.getKey()) {
            case MetaData.METADATA: {
                updateMetaData(value);
                break;
            }
            case Device.DEVICES: {
                updateDevices(value);
                break;
            }
            case Structure.STRUCTURES: {
                updateStructures(value);
                break;
            }
        }
    }

    @WorkerThread
    private void updateMetaData(Map<String, Object> metadataMap) {
        final MetaData metaData = parse(metadataMap, MetaData.class);
        if (metaData != null) {
            Timber.v("Update: metadata");
            metadataSubject.onNext(metaData);
        }
    }

    @WorkerThread
    private void updateDevices(Map<String, Object> devices) {
        for (Map.Entry<String, Object> entry : devices.entrySet()) {
            final Map<String, Object> value = (Map<String, Object>) entry.getValue();
            switch (entry.getKey()) {
                case SmokeCoAlarms.SMOKE_AND_CO_ALARMS: {
                    updateSmokeCo(value);
                    break;
                }
            }
        }
    }

    @WorkerThread
    private void updateStructures(Map<String, Object> structures) {
        for (Map.Entry<String, Object> entry : structures.entrySet()) {
            final Map<String, Object> value = (Map<String, Object>) entry.getValue();
            final Structure structure = parse(value, Structure.class);
            if (structure != null) {
                Timber.v("Update: structure");
                structureSubject.onNext(structure);
            }
        }
    }

    @WorkerThread
    private void updateSmokeCo(Map<String, Object> smokeCoAlarmsMap) {
        for (Map.Entry<String, Object> entry : smokeCoAlarmsMap.entrySet()) {
            final Map<String, Object> value = (Map<String, Object>) entry.getValue();
            final SmokeCoAlarms smokeCoAlarms = parse(value, SmokeCoAlarms.class);
            if (smokeCoAlarms != null) {
                Timber.v("Update: smokeCoAlarms");
                smokeCoAlarmsSubject.onNext(smokeCoAlarms);
            }
        }
    }

    private NestListener getListener() {
        return listenerRef == null ? null : listenerRef.get();
    }

    private boolean checkAuth() {
        if (isAuthenticated()) {
            return true;
        } else {
            login();
            return false;
        }
    }

    private void obtainAccessToken() {
        Timber.v("Starting auth flow...");
        final NestListener listener = getListener();
        if (listener != null) {
            listener.onInitiateLogin();
        }
    }

    private void authenticate(AccessToken token) {
        Timber.v("Authenticating...");
        getFirebase().auth(token.token, this);
    }

    private void setAuthenticated(boolean value) {
        if (authenticated != value) {
            authenticated = value;

            final NestListener listener = getListener();
            if (listener != null) {
                listener.onAuthStatusChanged(value);
            }
        }
    }

    private void subscribeUpdates() {
        Timber.v("Subscribing to updates...");
        getFirebase().addValueEventListener(this);
    }

    private void unsubscribeUpdates() {
        Timber.v("Unsubscribing from updates...");
        getFirebase().removeEventListener(this);
    }

    public void sendRequest(final String path, final Object value) {
        Observable
                .create(new Observable.OnSubscribe<Boolean>() {
                    @Override
                    public void call(Subscriber<? super Boolean> subscriber) {
                        getFirebase().child(path).setValue(value, null);
                        subscriber.onNext(true);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    @Override
    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
        Timber.v("Request completed, error: %s", firebaseError);
    }

    private Firebase getFirebase() {
        if (firebase == null) {
            LOCK.lock();
            try {
                if (firebase == null) {
                    Firebase.goOffline();
                    Firebase.goOnline();
                    firebase = new Firebase(NestApi.FIREBASE_NEST_URL);
                }
            } finally {
                LOCK.unlock();
            }
        }

        return firebase;
    }

    private static <T> T parse(Map<String, Object> map, Class<T> clazz) {
        try {
            return LoganSquare.parse(new JSONObject(map).toString(), clazz);
        } catch (Exception ex) {
            Timber.e(ex, "Unable to parse object");
        }
        return null;
    }

    @Override
    public void onAuthSuccess(Object o) {
        busy = false;
        Timber.v("Authentication succeeded.");
        setAuthenticated(true);
        subscribeUpdates();
    }

    @Override
    public void onAuthError(FirebaseError firebaseError) {
        busy = false;
        Timber.v("Authentication failed with error: " + firebaseError.toString());
        setAuthenticated(false);
    }

    @Override
    public void onAuthRevoked(FirebaseError firebaseError) {
        busy = false;
        Timber.v("Authentication revoked with error: " + firebaseError.toString());
        setAuthenticated(false);
        login();
    }

    // Marker for Firebase to retrieve a strongly-typed collection
    private static class StringObjectMapIndicator extends GenericTypeIndicator<Map<String, Object>> {

        static final StringObjectMapIndicator INSTANCE = new StringObjectMapIndicator();
    }
}
