package duy.phuong.handnote.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import duy.phuong.handnote.Listener.RecognitionCallback;
import duy.phuong.handnote.MyView.DrawingView.FingerDrawerView;
import duy.phuong.handnote.MyView.DrawingView.BitmapAdapter;
import duy.phuong.handnote.R;
import duy.phuong.handnote.Support.SupportUtils;

/**
 * Created by Phuong on 23/11/2015.
 */
public class TrainingFragment extends BaseFragment implements View.OnClickListener{
    private GridView mListDetectedBitmap;
    private BitmapAdapter mBitmapAdapter;
    private ArrayList<Bitmap> mListBitmap;
    private ImageButton mButtonSave;

    private FingerDrawerView mDrawer;

    public TrainingFragment() {
        this.mLayoutRes = R.layout.fragment_training;
        mListBitmap = new ArrayList<>();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mButtonSave = (ImageButton) mFragmentView.findViewById(R.id.buttonSave);
        mButtonSave.setOnClickListener(this);
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
        return TRAINING_FRAGMENT;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSave:
                for (Bitmap bitmap : mListBitmap) {
                    if (!SupportUtils.saveImage(bitmap, "", ".png")) {
                        Toast.makeText(mActivity, "Save images error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Toast.makeText(mActivity, "Done", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
