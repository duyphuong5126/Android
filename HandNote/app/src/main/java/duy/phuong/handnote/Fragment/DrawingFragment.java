package duy.phuong.handnote.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import duy.phuong.handnote.Listener.BackPressListener;
import duy.phuong.handnote.Listener.RecognitionCallback;
import duy.phuong.handnote.MyView.DrawingView.FingerDrawerView;
import duy.phuong.handnote.MyView.BitmapAdapter;
import duy.phuong.handnote.MyView.DrawingView.MyPath;
import duy.phuong.handnote.R;
import duy.phuong.handnote.Support.SupportUtils;

/**
 * Created by Phuong on 23/11/2015.
 */
public class DrawingFragment extends BaseFragment implements View.OnClickListener, BackPressListener {
    private GridView mListDetectedBitmap;
    private BitmapAdapter mBitmapAdapter;
    private ArrayList<Bitmap> mListBitmap;
    private ImageButton mButtonSave, mButtonEmpty, mButtonForward, mButtonEraser, mButtonUndo, mButtonRedo;
    private EditText mEdtName;

    private FingerDrawerView mDrawer;

    public DrawingFragment() {
        this.mLayoutRes = R.layout.fragment_drawing;
        mListBitmap = new ArrayList<>();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mButtonSave = (ImageButton) mFragmentView.findViewById(R.id.buttonSave);
        mButtonSave.setOnClickListener(this);
        mButtonEmpty = (ImageButton) mFragmentView.findViewById(R.id.buttonDelete);
        mButtonEmpty.setOnClickListener(this);
        mButtonUndo = (ImageButton) mFragmentView.findViewById(R.id.buttonUndo);
        mButtonUndo.setOnClickListener(this);
        mButtonRedo = (ImageButton) mFragmentView.findViewById(R.id.buttonRedo);
        mButtonRedo.setOnClickListener(this);
        mButtonForward = (ImageButton) mFragmentView.findViewById(R.id.buttonForward);
        mButtonForward.setOnClickListener(this);
        mButtonEraser = (ImageButton) mFragmentView.findViewById(R.id.buttonEraser);
        mButtonEraser.setOnClickListener(this);
        mEdtName = (EditText) mFragmentView.findViewById(R.id.edtName);
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
        return DRAWING_FRAGMENT;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSave:
                String currentName = mEdtName.getText().toString();
                for (Bitmap bitmap : mListBitmap) {
                    if (!SupportUtils.saveImage(bitmap, "Draw", currentName, ".png")) {
                        Toast.makeText(mActivity, "Save images error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Toast.makeText(mActivity, "Done", Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonDelete:
                emptyDrawer();
                break;
            case R.id.buttonForward:
                mListener.showFragment(BaseFragment.LEARNING_FRAGMENT);
                break;
            case R.id.buttonEraser:
                break;
            case R.id.buttonUndo:
                mDrawer.undo();
                break;
            case R.id.buttonRedo:
                mDrawer.redo();
                break;
            default:
                break;
        }
    }

    private void emptyDrawer() {
        mDrawer.emptyDrawer();
        mListBitmap.clear();
        mBitmapAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean doBack() {
        if (mListBitmap.size() > 0) {
            emptyDrawer();
            return true;
        }
        return false;
    }
}
