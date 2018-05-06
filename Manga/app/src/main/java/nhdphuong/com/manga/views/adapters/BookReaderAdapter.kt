package nhdphuong.com.manga.views.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ortiz.touchview.TouchImageView
import nhdphuong.com.manga.R
import nhdphuong.com.manga.supports.GlideUtils

/*
 * Created by nhdphuong on 5/5/18.
 */
class BookReaderAdapter(private val mContext: Context, private val mPageUrlList: List<String>,
                        private val mOnTapListener: View.OnClickListener) : PagerAdapter() {
    private val mPageMap: HashMap<Int, BookReaderViewHolder> = HashMap()

    init {
        mPageMap.clear()
    }

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val readerViewHolder = BookReaderViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_book_page, container, false),
                mPageUrlList[position]
        )
        mPageMap[position] = readerViewHolder
        container?.addView(readerViewHolder.view)
        readerViewHolder.ivPage.let { ivPage ->
            ivPage.setOnClickListener {
                mOnTapListener.onClick(ivPage)
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

    fun resetPage(page: Int) {
        mPageMap[page]?.ivPage?.let { ivPage ->
            if (ivPage.isZoomed) {
                ivPage.resetZoom()
            }
        }
    }

    private class BookReaderViewHolder(val view: View, pageUrl: String) {
        val ivPage: TouchImageView = view.findViewById(R.id.ivPage)

        init {
            GlideUtils.loadImage(pageUrl, R.drawable.ic_404_not_found, ivPage)
        }
    }
}