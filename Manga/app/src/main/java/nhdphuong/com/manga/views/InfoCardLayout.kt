package nhdphuong.com.manga.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import nhdphuong.com.manga.R
import nhdphuong.com.manga.data.entity.book.Tag
import nhdphuong.com.manga.supports.SupportUtils
import java.util.*

/*
 * Created by nhdphuong on 4/15/18.
 */
class InfoCardLayout(private val layoutInflater: LayoutInflater, private val tagList: List<Tag>, private val mContext: Context) {
    companion object {
        private val TAG = InfoCardLayout::class.java.simpleName
    }

    @SuppressLint("InflateParams")
    fun loadInfoList(viewGroup: ViewGroup) {
        if (tagList.isEmpty()) {
            return
        }
        var tagLine = layoutInflater.inflate(R.layout.item_tag_line, viewGroup, false).findViewById<LinearLayout>(R.id.lineRoot)
        viewGroup.addView(tagLine)
        val viewList = LinkedList<View>()
        for (tag in tagList) {
            val view = InfoCardViewHolder(layoutInflater.inflate(R.layout.item_tag, viewGroup, false), tag).view
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            viewList.add(view)
        }
        // viewList.sortWith(compareBy({ it.measuredWidth })) // TODO: review cards layout when it's not sorted
        val totalWidth = viewGroup.measuredWidth
        val totalMargin = SupportUtils.dp2Pixel(mContext, 6)
        Log.d(TAG, "Total width: $totalWidth")
        var widthCount = 0
        for (view in viewList) {
            if (view.measuredWidth > totalWidth - totalMargin) {
                view.layoutParams.width = totalWidth - totalMargin
            }
            val itemWidth = view.measuredWidth + totalMargin
            widthCount += itemWidth
            Log.d(TAG, "Item: $itemWidth, widthCount: $widthCount, total: $totalWidth")
            if (widthCount > totalWidth) {
                tagLine = layoutInflater.inflate(R.layout.item_tag_line, viewGroup, false).findViewById(R.id.lineRoot)
                viewGroup.addView(tagLine)
                widthCount = itemWidth
            }
            tagLine.addView(view)
        }
    }

    private inner class InfoCardViewHolder(val view: View, tag: Tag) : View.OnClickListener {
        private val mTvLabel: TextView = view.findViewById(R.id.tvLabel)
        private val mTvCount: TextView = view.findViewById(R.id.tvCount)

        init {
            mTvLabel.text = tag.name
            mTvCount.text = String.format(mContext.getString(R.string.count), SupportUtils.formatBigNumber(tag.count))
        }

        override fun onClick(p0: View?) {

        }
    }
}