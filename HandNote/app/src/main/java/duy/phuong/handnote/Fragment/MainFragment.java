package duy.phuong.handnote.Fragment;

import android.os.Bundle;

import duy.phuong.handnote.R;

/**
 * Created by Phuong on 26/01/2016.
 */
public class MainFragment extends BaseFragment {

    public MainFragment() {
        this.mLayoutRes = R.layout.fragment_main;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public String fragmentIdentify() {
        return BaseFragment.MAIN_FRAGMENT;
    }
}
