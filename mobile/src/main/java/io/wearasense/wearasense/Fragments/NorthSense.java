package io.wearasense.wearasense.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import io.wearasense.wearasense.Base.WearasenseApplication;
import io.wearasense.wearasense.Interfaces.NorthSelect;
import io.wearasense.wearasense.Interfaces.PoiUpdate;
import io.wearasense.wearasense.R;

/**
 * Created by goofyahead on 8/07/15.
 */
public class NorthSense extends Fragment {

    private NorthSelect mCallback;

    public static NorthSense getInstance() {
        NorthSense nowfies = new NorthSense();
        Bundle args = new Bundle();
        nowfies.setArguments(args);
        return nowfies;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.north_sense_fragment, container, false);
        ButterKnife.inject(this, v);
        ((WearasenseApplication) getActivity().getApplication()).inject(this);
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (NorthSelect) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + NorthSelect.class.getName() + " interface");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCallback.northSelected();
    }
}
