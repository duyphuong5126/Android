package nhdphuong.com.manga.views.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import nhdphuong.com.manga.R
import nhdphuong.com.manga.supports.GlideUtils

/*
 * Created by nhdphuong on 5/5/18.
 */
class BookReaderAdapter(private val mContext: Context, private val mPageUrlList: List<String>,
                        private val mOnTapListener: View.OnClickListener) : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val readerViewHolder = BookReaderViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_book_page, container, false),
                mPageUrlList[position]
        )
        readerViewHolder.view.let { view ->
            container?.addView(view)
            view.setOnClickListener {
                mOnTapListener.onClick(view)
            }
        }
        return readerViewHolder.view
    }

    override fun isViewFromObject(view: View?, `object`: Any?): Boolean = view == `object`

    override fun getCount(): Int = mPageUrlList.size

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        container?.removeView(`object` as View)
    }

    override fun getPageTitle(position: Int): CharSequence = "Page number ${position + 1}"

    private class BookReaderViewHolder(val view: View, private val pageUrl: String) {
        private val ivPage: ImageView = view.findViewById(R.id.ivPage)

        init {
            GlideUtils.loadImage(pageUrl, R.drawable.ic_404_not_found, ivPage)
        }
    }
}