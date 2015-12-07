package nest.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyfishjy.library.RippleBackground;

import javax.inject.Inject;

import nest.App;
import nest.R;
import nest.model.AlarmState;
import nest.model.AwayState;
import nest.model.BatteryState;
import nest.model.SmokeCoAlarms;
import nest.model.Structure;
import nest.model.UiColorState;
import nest.service.nest.NestManager;
import nest.ui.HomeView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class MainFragment extends BaseFragment {
    public static final String TAG = MainFragment.class.getSimpleName();

    @Inject
    NestManager nestManager;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    private ViewHolder viewHolder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        App.getGraph().inject(this);

        showProgress();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        viewHolder = new ViewHolder(root);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewHolder = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        subscribeOnStructureUpdate();
        subscribeOnSmokeCoAlarmsUpdate();

        nestManager.startListen(nestListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        subscriptions.clear();
        nestManager.stopListen(nestListener);
    }

    private void subscribeOnStructureUpdate() {
        Subscription structureSub = nestManager.getStructureObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Structure>() {
                    @Override
                    public void call(Structure structure) {
                        viewHolder.updateStructure(structure);
                    }
                });
        subscriptions.add(structureSub);
    }

    private void subscribeOnSmokeCoAlarmsUpdate() {
        Subscription smokeCoAlarmsSub = nestManager.getSmokeCoAlarmsObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SmokeCoAlarms>() {
                    @Override
                    public void call(SmokeCoAlarms smokeCoAlarms) {
                        viewHolder.updateSmokeCoAlarms(smokeCoAlarms);
                    }
                });

        subscriptions.add(smokeCoAlarmsSub);
    }

    @SuppressWarnings("deprecation") // min api is 16
    public class ViewHolder implements View.OnClickListener {

        private Structure structure;
        private SmokeCoAlarms smokeCoAlarms;

        final View root;
        final HomeView homeButton;
        final RippleBackground rippleBackground;
        final TextView titleText;
        final ImageView logoutButton;
        final TextView stateText;

        public ViewHolder(View root) {
            this.root = root;

            rippleBackground = (RippleBackground) root.findViewById(R.id.nest_home_ripple_animation);
            homeButton = (HomeView) root.findViewById(R.id.nest_home_button);
            titleText = (TextView) root.findViewById(R.id.nest_title);
            logoutButton = (ImageView) root.findViewById(R.id.nest_logout_button);
            stateText = (TextView) root.findViewById(R.id.nest_state);

            homeButton.setOnClickListener(this);
            logoutButton.setOnClickListener(this);
        }

        public void updateStructure(Structure structure) {
            hideProgress();
            this.structure = structure;

            if (smokeCoAlarms != null && (UiColorState.FIELD_RED.equals(smokeCoAlarms.uiColorState) || UiColorState.FIELD_YELLOW.equals(smokeCoAlarms.uiColorState))) {
                return;
            }

            homeButton.setImageResource(AwayState.FIELD_AWAY.equals(structure.awayState) || AwayState.FIELD_AUTO_AWAY.equals(structure.awayState)
                    ? R.drawable.ic_nest_away
                    : R.drawable.ic_nest_home);
        }

        public void updateSmokeCoAlarms(SmokeCoAlarms smokeCoAlarms) {
            hideProgress();

            this.smokeCoAlarms = smokeCoAlarms;
            titleText.setText(smokeCoAlarms.name);

            Resources res = getResources();

            switch (smokeCoAlarms.uiColorState) {
                case UiColorState.FIELD_GREEN: {
                    stateText.setTextColor(res.getColor(R.color.green));
                    homeButton.setColor(res.getColor(R.color.green));
                    rippleBackground.stopRippleAnimation();
                    break;
                }
                case UiColorState.FIELD_YELLOW: {
                    stateText.setTextColor(res.getColor(R.color.yellow));
                    homeButton.setColor(res.getColor(R.color.yellow));
                    homeButton.setImageResource(R.drawable.ic_nest_home_warning);
                    rippleBackground.stopRippleAnimation();
                    break;
                }
                case UiColorState.FIELD_RED: {
                    stateText.setTextColor(res.getColor(R.color.red));
                    homeButton.setColor(res.getColor(R.color.red));
                    homeButton.setImageResource(R.drawable.ic_nest_home_warning);
                    rippleBackground.startRippleAnimation();
                    break;
                }
            }

            String testing;
            if (smokeCoAlarms.isManualTestActive) {
                homeButton.setColor(res.getColor(R.color.blue));
                testing = res.getString(R.string.test_in_progress);
            } else {
                testing = "";
            }


            String coState = getAlarmState(smokeCoAlarms.coAlarmState, res);
            String smokeState = getAlarmState(smokeCoAlarms.smokeAlarmState, res);
            String battery = getHealthState(smokeCoAlarms.batteryHealth, res);

            stateText.setText(res.getString(R.string.smoke_co_alarms_state, testing, coState, smokeState, battery));
        }

        private String getAlarmState(String state, Resources res) {
            switch (state) {
                case AlarmState.FIELD_OK: {
                    return res.getString(R.string.alarm_state_ok);
                }
                case AlarmState.FIELD_WARNING: {
                    return res.getString(R.string.alarm_state_warning);
                }
                case AlarmState.FIELD_EMERGENCY: {
                    //break; commented specially!
                }
                default: {
                    return res.getString(R.string.alarm_state_emergency);
                }
            }
        }


        private String getHealthState(String state, Resources res) {
            switch (state) {
                case BatteryState.FIELD_OK: {
                    return res.getString(R.string.battery_state_ok);
                }
                case BatteryState.FIELD_REPLACE: {
                    //break; commented specially!
                }
                default: {
                    return res.getString(R.string.battery_state_replace);
                }
            }
        }

        @Override
        public void onClick(View v) {
            final int id = v.getId();
            switch (id) {
                case R.id.nest_home_button: {
                    toggleAway();
                    break;
                }
                case R.id.nest_logout_button: {
                    nestManager.logout();
                    break;
                }
            }
        }

        public void setAway() {
            structure.awayState = AwayState.FIELD_AWAY;
            homeButton.setImageResource(R.drawable.ic_nest_away);
            nestManager.sendRequest(structure.getPath(Structure.FIELD_AWAY), AwayState.FIELD_AWAY);
        }

        public void setHome() {
            structure.awayState = AwayState.FIELD_HOME;
            homeButton.setImageResource(R.drawable.ic_nest_home);
            nestManager.sendRequest(structure.getPath(Structure.FIELD_AWAY), AwayState.FIELD_HOME);
        }

        public void toggleAway() {
            if (structure == null) {
                return;
            }

            switch (structure.awayState) {
                case AwayState.FIELD_AUTO_AWAY: {
                    //break; commented specially!!!
                }
                case AwayState.FIELD_AWAY: {
                    setHome();
                    break;
                }
                case AwayState.FIELD_HOME: {
                    //break; commented specially!!!
                }
                default: {
                    setAway();
                    break;
                }
            }
        }
    }

}
