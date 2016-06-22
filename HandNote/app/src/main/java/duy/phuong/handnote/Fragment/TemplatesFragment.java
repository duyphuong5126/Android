package duy.phuong.handnote.Fragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import duy.phuong.handnote.MyView.BitmapAdapter;
import duy.phuong.handnote.MyView.ExpandableGridView;
import duy.phuong.handnote.R;

/**
 * Created by Phuong on 10/05/2016.
 */
public class TemplatesFragment extends BaseFragment {
    private int mHolderHeight;
    private LinearLayout mLayoutHolder;

    public TemplatesFragment() {
        mLayoutRes = R.layout.fragment_templates;
        mHolderHeight = 0;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHolderHeight = getArguments().getInt("TabHeight");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<Bitmap> mListBitmap = new ArrayList<>();
        Resources resources = mActivity.getResources();
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._1_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._2_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._3_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._4_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._5_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._6_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._7_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._8_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._9_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._a_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._a1_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._b_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._b1_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._b2_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._c_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._d_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._d1_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._e_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._e1_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._f_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._f1_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._g_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._g1_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._h_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._h1_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._i_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._i1_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._j_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._j1_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._k_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._k1_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._k2_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._l_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._l1_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._m_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._m1_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._n_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._n1_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._o_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._p_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._q_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._q1_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._r_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._r1_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._s_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._t_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._t1_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._u_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._u1_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._v_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._w_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._x_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._x1_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._y_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._y1_));
        mListBitmap.add(BitmapFactory.decodeResource(resources, R.drawable._z_));
        BitmapAdapter mBitmapAdapter = new BitmapAdapter(mActivity, R.layout.item_bitmap_1, mListBitmap);
        Log.d("Size", "" + mListBitmap.size());
        ExpandableGridView mListTemplates = (ExpandableGridView) mFragmentView.findViewById(R.id.listTemplateBitmaps);
        mLayoutHolder = (LinearLayout) mFragmentView.findViewById(R.id.layoutHolder);
        mListTemplates.setExpanded(true);
        mListTemplates.setAdapter(mBitmapAdapter);
        mBitmapAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        mLayoutHolder.getLayoutParams().height = mHolderHeight;
    }

    @Override
    public String fragmentIdentify() {
        return BaseFragment.TEMPLATES_FRAGMENT;
    }
}
