package com.huy.monthlyfinance.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import com.huy.monthlyfinance.Listener.NavigationListener;
import com.huy.monthlyfinance.MyView.BasicAdapter;
import com.huy.monthlyfinance.MyView.Item.ListItem.TransferItem;
import com.huy.monthlyfinance.R;
import com.huy.monthlyfinance.SupportUtils.SupportUtils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Phuong on 19/11/2016.
 */

public class BudgetFragment extends BaseFragment implements View.OnClickListener{
    private LinearLayout mLayoutAdd;
    private Animation mAnimationForward, mAnimationBackward;
    private Animation mAnimationRotateForward30, mAnimationRotateBackward30;
    private LinearLayout mLayoutQuickAdd;

    private ArrayList<TransferItem> mListTransfer;
    private BasicAdapter<TransferItem> mTransferAdapter;
    private ListView mListTransaction;

    private LinearLayout mLayoutAddIncome;

    private ScrollView mMainScroll;

    @Override
    protected int getLayoutXML() {
        return R.layout.fragment_budget;
    }

    @Override
    protected void onPrepare() {
        mListTransfer = new ArrayList<>();
        mListTransfer.add(new TransferItem(100, "Cash", "Bank", 1000, 700, new Date(System.currentTimeMillis())));
        mListTransfer.add(new TransferItem(50, "Cash", "Bank", 900, 800, new Date(System.currentTimeMillis())));
        mListTransfer.add(new TransferItem(10, "Credit", "Cash", 900, 850, new Date(System.currentTimeMillis())));
        mListTransfer.add(new TransferItem(40, "Credit", "Cash", 890, 860, new Date(System.currentTimeMillis())));
    }

    @Override
    protected void initUI(final View view) {
        mMainScroll = (ScrollView) view.findViewById(R.id.scrollBudget);
        mLayoutAddIncome = (LinearLayout) view.findViewById(R.id.layoutAddIncome);
        view.findViewById(R.id.layoutButtonAddIncome).setOnClickListener(this);
        view.findViewById(R.id.layoutButtonAddTransfer).setOnClickListener(this);
        view.findViewById(R.id.buttonBack).setOnClickListener(this);

        final ImageButton mButtonAddTransfer = (ImageButton) view.findViewById(R.id.buttonAddTransfer);
        final ImageButton mButtonAddCash = (ImageButton) view.findViewById(R.id.buttonAddCash);
        RadioGroup mTabHost = (RadioGroup) view.findViewById(R.id.tabHost);
        final RadioButton buttonTabCash = (RadioButton) view.findViewById(R.id.buttonTabCash);
        final RadioButton buttonTabBank = (RadioButton) view.findViewById(R.id.buttonTabBank);
        final RadioButton buttonTabCredit = (RadioButton) view.findViewById(R.id.buttonTabCredit);
        mTabHost.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                buttonTabCash.setChecked(R.id.buttonTabCash == i);
                buttonTabBank.setChecked(R.id.buttonTabBank == i);
                buttonTabCredit.setChecked(R.id.buttonTabCredit == i);
                switch (i) {
                    case R.id.buttonTabCash:
                        view.findViewById(R.id.tabCash).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.tabBank).setVisibility(View.GONE);
                        view.findViewById(R.id.tabCredit).setVisibility(View.GONE);
                        break;
                    case R.id.buttonTabBank:
                        view.findViewById(R.id.tabCash).setVisibility(View.GONE);
                        view.findViewById(R.id.tabBank).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.tabCredit).setVisibility(View.GONE);
                        break;
                    default:
                        view.findViewById(R.id.tabCash).setVisibility(View.GONE);
                        view.findViewById(R.id.tabBank).setVisibility(View.GONE);
                        view.findViewById(R.id.tabCredit).setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        view.findViewById(R.id.buttonAdd).setOnClickListener(this);
        mLayoutQuickAdd = (LinearLayout) view.findViewById(R.id.layoutQuickAddSelect);
        mLayoutQuickAdd.setOnClickListener(this);
        mLayoutAdd = (LinearLayout) view.findViewById(R.id.layoutButtonAdd);
        mLayoutAdd.setOnClickListener(this);
        mAnimationRotateForward30 = new RotateAnimation(0.0f, 30.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mAnimationRotateForward30.setDuration(1000);
        mAnimationRotateForward30.setRepeatCount(0);
        mAnimationRotateForward30.setRepeatMode(Animation.REVERSE);
        mAnimationRotateForward30.setFillAfter(true);
        mAnimationRotateBackward30 = new RotateAnimation(0.0f, -30.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mAnimationRotateBackward30.setDuration(1000);
        mAnimationRotateBackward30.setRepeatCount(0);
        mAnimationRotateBackward30.setRepeatMode(Animation.REVERSE);
        mAnimationRotateBackward30.setFillAfter(true);

        mAnimationForward = new RotateAnimation(0.0f, 45.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mAnimationForward.setDuration(500);
        mAnimationForward.setRepeatCount(0);
        mAnimationForward.setRepeatMode(Animation.REVERSE);
        mAnimationForward.setFillAfter(true);
        mAnimationForward.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLayoutQuickAdd.setVisibility(View.VISIBLE);
                mButtonAddTransfer.startAnimation(mAnimationRotateForward30);
                mButtonAddCash.startAnimation(mAnimationRotateForward30);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mAnimationBackward = new RotateAnimation(45.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimationBackward.setDuration(500);
        mAnimationBackward.setRepeatCount(0);
        mAnimationBackward.setRepeatMode(Animation.REVERSE);
        mAnimationBackward.setFillAfter(true);
        mAnimationBackward.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mLayoutQuickAdd.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mButtonAddTransfer.startAnimation(mAnimationRotateBackward30);
                mButtonAddCash.startAnimation(mAnimationRotateBackward30);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mTransferAdapter = new BasicAdapter<>(mListTransfer, R.layout.item_transfer, inflater);
        mListTransaction = (ListView) view.findViewById(R.id.listTransaction);
        mListTransaction.setAdapter(mTransferAdapter);
    }

    @Override
    protected void setStatusBarColor() {
        mListener.setStatusBarColor(Color.parseColor("#8bc34a"));
    }

    @Override
    protected int getSideMenuColor() {
        return Color.parseColor("#8bc34a");
    }

    @Override
    protected void fragmentReady(Bundle savedInstanceState) {
        SupportUtils.setListViewHeight(mListTransaction);
        mMainScroll.smoothScrollTo(0, 0);
    }

    @Override
    protected boolean canGoBack() {
        return mLayoutAddIncome.getVisibility() != View.VISIBLE;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonBack:
                if (canGoBack()) {
                    mNavListener.navBack();
                } else {
                    mLayoutAddIncome.setVisibility(View.GONE);
                }
                break;
            case R.id.buttonAdd:
            case R.id.layoutButtonAdd:
                mLayoutAdd.startAnimation(mAnimationForward);
                break;
            case R.id.layoutQuickAddSelect:
                mLayoutAdd.startAnimation(mAnimationBackward);
                break;
            case R.id.layoutButtonAddIncome:
                mLayoutAddIncome.setVisibility(View.VISIBLE);
                mLayoutAdd.startAnimation(mAnimationBackward);
                break;
            case R.id.layoutButtonAddTransfer:
                break;
        }
    }

    @Override
    public void refreshData() {

    }
}