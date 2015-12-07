package nest.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import nest.service.nest.NestListener;

public class BaseFragment extends Fragment {

    NestListener nestListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        nestListener = (NestListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        nestListener = null;
    }

    void showProgress() {
        FragmentManager manager = getChildFragmentManager();
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        fragment.show(manager, ProgressDialogFragment.TAG);
    }

    void hideProgress() {
        ProgressDialogFragment fragment = (ProgressDialogFragment) getChildFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG);
        if (fragment != null) {
            fragment.dismiss();
        }
    }
}
