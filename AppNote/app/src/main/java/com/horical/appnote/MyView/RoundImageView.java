package com.horical.appnote.MyView;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Phuong on 25/07/2015.
 */
public class RoundImageView extends ImageView {
    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getDrawable() == null || getWidth() == 0 || getHeight() == 0) return;
        Bitmap drawable = ((BitmapDrawable) getDrawable()).getBitmap();
        Bitmap src = drawable.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap roundedBitmap = RoundImageView.getRoundeditmap(src, (getWidth() >= getHeight()) ? getHeight() : getWidth());
        canvas.drawColor(((ColorDrawable) getBackground()).getColor());
        canvas.drawBitmap(roundedBitmap, (getWidth() - roundedBitmap.getWidth()) / 2, (getHeight() - roundedBitmap.getHeight()) / 2, null);
    }

    @Override
    public boolean isInEditMode() {
        return super.isInEditMode();
    }

    public static Bitmap getRoundeditmap(Bitmap src, int radius) {
        Bitmap roundBitmap = null;
        roundBitmap = (src.getWidth() == radius || src.getHeight() == radius) ? src : Bitmap.createScaledBitmap(src, radius, radius, false);
        Bitmap des = Bitmap.createBitmap(roundBitmap.getWidth(), roundBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(des);
        final Rect rect = new Rect(0, 0, des.getWidth(), des.getHeight());

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(Color.parseColor("#BAB399"));

        canvas.drawCircle(des.getWidth() / 2 + 0.7f, des.getHeight() / 2 + 0.7f, des.getWidth() / 2 - 10f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(roundBitmap, rect, rect, paint);

        return des;
    }
}
