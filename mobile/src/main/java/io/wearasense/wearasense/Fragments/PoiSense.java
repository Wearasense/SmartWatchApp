package io.wearasense.wearasense.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import io.wearasense.wearasense.Base.WearasenseApplication;
import io.wearasense.wearasense.Interfaces.PoiUpdate;
import io.wearasense.wearasense.R;

/**
 * Created by goofyahead on 8/07/15.
 */
public class PoiSense extends Fragment{

    private PoiUpdate mCallback;

    public static PoiSense getInstance() {
        PoiSense nowfies = new PoiSense();
        Bundle args = new Bundle();
        nowfies.setArguments(args);
        return nowfies;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.poi_sense_fragment, container, false);
        ButterKnife.inject(this, v);
        ((WearasenseApplication) getActivity().getApplication()).inject(this);
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (PoiUpdate) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + PoiUpdate.class.getName() + " interface");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCallback.poiUpdated(40.447226f, -3.800142f);
    }
}
