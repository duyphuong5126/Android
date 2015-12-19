package duy.phuong.handnote.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;

import duy.phuong.handnote.Listener.RecognitionCallback;
import duy.phuong.handnote.MyView.DrawingView.FingerDrawerView;
import duy.phuong.handnote.MyView.DrawingView.BitmapAdapter;
import duy.phuong.handnote.R;

/**
 * Created by Phuong on 23/11/2015.
 */
public class CreateTextFragment extends BaseFragment{
    private GridView mListDetectedBitmap;
    private BitmapAdapter mBitmapAdapter;
    private ArrayList<Bitmap> mListBitmap;

    private FingerDrawerView mDrawer;

    public CreateTextFragment() {
        this.mLayoutRes = R.layout.fragment_create;
        mListBitmap = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListDetectedBitmap = (GridView) mFragmentView.findViewById(R.id.listDetectedBitmap);

        mBitmapAdapter = new BitmapAdapter(mActivity, 0, mListBitmap);
        mListDetectedBitmap.setAdapter(mBitmapAdapter);

        mDrawer = (FingerDrawerView) mFragmentView.findViewById(R.id.FingerDrawer);
        mDrawer.setListener(new RecognitionCallback() {
            @Override
            public void onRecognizeSuccess(ArrayList<Bitmap> listBitmaps) {
                mListBitmap.clear();
                for (Bitmap bitmap : listBitmaps) {
                    mListBitmap.add(bitmap);
                    mBitmapAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public String fragmentIdentify() {
        return CREATE_TEXT_FRAGMENT;
    }
}
