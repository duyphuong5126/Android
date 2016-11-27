package com.huy.monthlyfinance.MyView.Item.ListItem;

import android.view.View;
import android.widget.TextView;

import com.huy.monthlyfinance.R;

import java.util.Date;

/**
 * Created by Phuong on 27/11/2016.
 */

public class TransferItem extends BaseItem {
    private double mAmount;
    private String mSource;
    private String mTarget;
    private double mSourceBefore;
    private double mTargetBefore;
    private Date mTransactionTime;

    public TransferItem(double mAmount, String mSource, String mTarget, double mSourceBefore, double mTargetBefore, Date mTransactionTime) {
        this.mAmount = mAmount;
        this.mSource = mSource;
        this.mTarget = mTarget;
        this.mSourceBefore = mSourceBefore;
        this.mTargetBefore = mTargetBefore;
        this.mTransactionTime = mTransactionTime;
    }

    @Override
    public void setView(View view) {
        TextView sourceBefore = (TextView) view.findViewById(R.id.sourceBefore);
        TextView sourceAfter = (TextView) view.findViewById(R.id.sourceAfter);
        TextView targetBefore = (TextView) view.findViewById(R.id.targetBefore);
        TextView targetAfter = (TextView) view.findViewById(R.id.targetAfter);
        TextView amount = (TextView) view.findViewById(R.id.textAmount);
        TextView transactionTime = (TextView) view.findViewById(R.id.textTransactionTime);
        TextView source = (TextView) view.findViewById(R.id.textSource);
        TextView target = (TextView) view.findViewById(R.id.textTarget);

        sourceBefore.setText("$" + mSourceBefore);
        sourceAfter.setText("$" + (mSourceBefore - mAmount));
        targetBefore.setText("$" + mTargetBefore);
        targetAfter.setText("$" + (mTargetBefore + mAmount));
        amount.setText("$" + mAmount);
        transactionTime.setText(mTransactionTime.toString());
        source.setText(mSource);
        target.setText(mTarget);
    }
}
