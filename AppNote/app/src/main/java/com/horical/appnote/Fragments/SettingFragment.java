package com.horical.appnote.Fragments;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;

import com.horical.appnote.LocalStorage.ApplicationSharedData;
import com.horical.appnote.R;
import com.horical.appnote.Supports.LanguageUtils;

/**
 * Created by Phuong on 30/07/2015.
 */
public class SettingFragment extends BaseFragment {
    private LinearLayout mLayoutSettingLanguage;
    private LinearLayout mLayoutUseGuide;
    private TextView mTvCurrentUser, mTvCurrentLanguage;
    private TextView mTvCurrentUserTitle, mTvCurrentLanguageTitle, mTvUseGuideTitle, mTvAutoSaveTitle;

    public SettingFragment() {
        this.mLayout_xml_id = R.layout.fragment_setting;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTvCurrentUser = (TextView) mFragmentView.findViewById(R.id.tvCurrentUser);
        mTvCurrentLanguage = (TextView) mFragmentView.findViewById(R.id.tvCurrentLanguage);
        mTvCurrentUserTitle = (TextView) mFragmentView.findViewById(R.id.tvCurrentUserTitle);
        mTvCurrentLanguageTitle = (TextView) mFragmentView.findViewById(R.id.tvCurrentLanguageTitle);
        mTvUseGuideTitle = (TextView) mFragmentView.findViewById(R.id.tvHowToUseTitle);
        mTvAutoSaveTitle = (TextView) mFragmentView.findViewById(R.id.tvAutoSave);

        LinearLayout mLayoutSettingAccount = (LinearLayout) mFragmentView.findViewById(R.id.layoutSettingAccount);
        mLayoutSettingAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainInterface.ChangeFragment(BaseFragment.SettingsFragment, BaseFragment.ViewAccountFragment);
            }
        });
        mLayoutSettingLanguage = (LinearLayout) mFragmentView.findViewById(R.id.layoutSettingLanguage);
        mLayoutSettingLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(mActivity, mLayoutSettingLanguage);
                menu.getMenuInflater().inflate(R.menu.menu_language_list, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.itemVietnamese:
                                LanguageUtils.setCurrentLanguage(LanguageUtils.VIETNAMESE);
                                break;
                            case R.id.itemEnglish:
                                LanguageUtils.setCurrentLanguage(LanguageUtils.ENGLISH);
                                break;
                        }
                        mMainInterface.restartApplication();
                        return false;
                    }
                });
                menu.show();
            }
        });
        mLayoutUseGuide = (LinearLayout) mFragmentView.findViewById(R.id.layoutUseGuide);

        Switch mChooseSaveOnExit = (Switch) mFragmentView.findViewById(R.id.chooseSaveOnExit);
        mChooseSaveOnExit.setChecked(ApplicationSharedData.isAutoSave());
        mChooseSaveOnExit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ApplicationSharedData.setAutoSave(isChecked);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mTvCurrentUser.setText(ApplicationSharedData.getDisplayname());
        mTvCurrentLanguage.setText(ApplicationSharedData.getLanguage());
        mTvCurrentUserTitle.setText(LanguageUtils.getAccountString());
        mTvCurrentLanguageTitle.setText(LanguageUtils.getLanguageString());
        mTvUseGuideTitle.setText(LanguageUtils.getUseGuideString());
        mTvAutoSaveTitle.setText(LanguageUtils.getAutoSaveString());
    }

    @Override
    public void updateUI() {

    }
}
