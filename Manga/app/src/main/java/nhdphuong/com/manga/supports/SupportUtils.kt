package nhdphuong.com.manga.supports

import android.content.Context
import android.util.TypedValue


class SupportUtils {
    companion object {
        fun dp2Pixel(context: Context, dp: Int): Int =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp * 1F, context.resources.displayMetrics).toInt()
    }
}