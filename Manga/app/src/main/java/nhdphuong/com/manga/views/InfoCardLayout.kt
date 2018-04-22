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
        val totalWidth = viewGroup.measuredWidth
        val totalMargin = SupportUtils.dp2Pixel(mContext, 6)
        if (viewList.size >= 3) {
            sortViewList(viewList, totalWidth, totalMargin / 2)
        }
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

    private fun sortViewList(viewList: LinkedList<View>, totalWidth: Int, totalMargin: Int) {
        var anchorId = 0
        val size = viewList.size
        while (anchorId < size - 1) {
            var widthSum = viewList[anchorId].measuredWidth + totalMargin
            var viewId = anchorId + 1
            val suitableViews = LinkedList<Int>()
            while (widthSum <= totalWidth && viewId < size) {
                widthSum += viewList[viewId].measuredWidth + totalMargin
                if (widthSum <= totalWidth) {
                    suitableViews.add(viewId)
                } else {
                    widthSum += viewList[viewId].measuredWidth + totalMargin
                }
                viewId++
            }
            if (suitableViews.isEmpty()) {
                anchorId++
            } else {
                var idOffset = 1
                for (i in suitableViews) {
                    Collections.swap(viewList, i, anchorId + idOffset)
                    idOffset++
                }
                anchorId += suitableViews.size + 1
            }
        }
    }
}